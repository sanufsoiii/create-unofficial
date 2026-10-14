package com.simibubi.create.content.kinetics.steamEngine;

import java.lang.ref.WeakReference;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity.RotationDirection;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SteamEngineBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

	protected ScrollOptionBehaviour<RotationDirection> movementDirection;

	public WeakReference<PoweredShaftBlockEntity> target;
	public WeakReference<FluidTankBlockEntity> source;

	float prevAngle = 0;

	public SteamEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		source = new WeakReference<>(null);
		target = new WeakReference<>(null);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		movementDirection = new ScrollOptionBehaviour<>(RotationDirection.class,
			CreateLang.translateDirect("contraptions.windmill.rotation_direction"), this, new SteamEngineValueBox());
		movementDirection.onlyActiveWhen(() -> {
			PoweredShaftBlockEntity shaft = getShaft();
			return shaft == null || !shaft.hasSource();
		});
		movementDirection.withCallback($ -> onDirectionChanged());
		behaviours.add(movementDirection);

		// TODO: registerAwardables(behaviours, AllAdvancements.STEAM_ENGINE);
	}

	private void onDirectionChanged() {
	}

	@Override
	public void tick() {
		super.tick();
		FluidTankBlockEntity tank = getTank();
		PoweredShaftBlockEntity shaft = getShaft();

		if (tank == null || shaft == null || !isValid()) {
			if (level.isClientSide())
				return;
			if (shaft == null)
				return;
			if (!shaft.getBlockPos()
				.subtract(worldPosition)
				.equals(shaft.enginePos))
				return;
			if (shaft.engineEfficiency == 0)
				return;
			Direction facing = SteamEngineBlock.getFacing(getBlockState());
			if (level.isLoaded(worldPosition.relative(facing.getOpposite())))
				shaft.update(worldPosition, 0, 0);
			return;
		}

		boolean verticalTarget = false;
		BlockState shaftState = shaft.getBlockState();
		Axis targetAxis = Axis.X;
		if (shaftState.getBlock() instanceof IRotate ir)
			targetAxis = ir.getRotationAxis(shaftState);
		verticalTarget = targetAxis == Axis.Y;

		BlockState blockState = getBlockState();
		if (!AllBlocks.STEAM_ENGINE.has(blockState))
			return;
		Direction facing = SteamEngineBlock.getFacing(blockState);
		if (facing.getAxis() == Axis.Y)
			facing = blockState.getValue(SteamEngineBlock.FACING);

		float efficiency = Mth.clamp(tank.boiler.getEngineEfficiency(tank.getTotalTankSize()), 0, 1);
		// if (efficiency > 0)
		// 	award(AllAdvancements.STEAM_ENGINE);

		int conveyedSpeedLevel =
			efficiency == 0 ? 1 : verticalTarget ? 1 : (int) GeneratingKineticBlockEntity.convertToDirection(1, facing);
		if (targetAxis == Axis.Z)
			conveyedSpeedLevel *= -1;
		if (movementDirection.get() == RotationDirection.COUNTER_CLOCKWISE)
			conveyedSpeedLevel *= -1;

		float shaftSpeed = shaft.getTheoreticalSpeed();
		if (shaft.hasSource() && shaftSpeed != 0 && conveyedSpeedLevel != 0
			&& (shaftSpeed > 0) != (conveyedSpeedLevel > 0)) {
			movementDirection.setValue(1 - movementDirection.get()
				.ordinal());
			conveyedSpeedLevel *= -1;
		}

		shaft.update(worldPosition, conveyedSpeedLevel, efficiency);

		if (!level.isClientSide)
			return;

		spawnParticles();
	}

	@Override
	public void remove() {
		PoweredShaftBlockEntity shaft = getShaft();
		if (shaft != null)
			shaft.remove(worldPosition);
		super.remove();
	}

	@Nullable
	public PoweredShaftBlockEntity getShaft() {
		PoweredShaftBlockEntity shaft = target.get();
		if (shaft == null || shaft.isRemoved() || !shaft.canBePoweredBy(worldPosition)) {
			if (shaft != null)
				target = new WeakReference<>(null);
			Direction facing = SteamEngineBlock.getFacing(getBlockState());
			BlockPos shaftPos = SteamEngineBlock.getShaftPos(getBlockState(), worldPosition);
			BlockState shaftState = level.getBlockState(shaftPos);
			if (!SteamEngineBlock.isShaftValid(getBlockState(), shaftState))
				return null;
			if (level.getBlockEntity(shaftPos) instanceof PoweredShaftBlockEntity psbe) {
				if (!psbe.canBePoweredBy(worldPosition))
					return null;
				target = new WeakReference<>(shaft = psbe);
				return shaft;
			}
			return null;
		}
		return shaft;
	}

	@Nullable
	public FluidTankBlockEntity getTank() {
		FluidTankBlockEntity tank = source.get();
		if (tank == null || tank.isRemoved()) {
			if (tank != null)
				source = new WeakReference<>(null);
			Direction facing = SteamEngineBlock.getFacing(getBlockState());
			BlockPos tankPos = worldPosition.relative(facing.getOpposite());
			if (level.getBlockEntity(tankPos) instanceof FluidTankBlockEntity fte) {
				source = new WeakReference<>(tank = fte);
				return tank;
			}
			return null;
		}
		return tank;
	}

	public boolean isValid() {
		BlockState blockState = getBlockState();
		if (!AllBlocks.STEAM_ENGINE.has(blockState))
			return false;
		Direction facing = SteamEngineBlock.getFacing(blockState);
		PoweredShaftBlockEntity shaft = getShaft();
		FluidTankBlockEntity tank = getTank();
		return shaft != null && tank != null && tank.boiler != null && tank.boiler.isActive();
	}

	private void distortPistonPoles(PoweredShaftBlockEntity shaft, boolean verticalTarget) {
	}

	private void spawnParticles() {
		Float targetAngle = getTargetAngle();
		PoweredShaftBlockEntity ste = target.get();
		if (ste == null)
			return;
		if (!ste.isPoweredBy(worldPosition) || ste.engineEfficiency == 0)
			return;
		if (targetAngle == null)
			return;

		float angle = AngleHelper.deg(targetAngle);
		angle += (angle < 0) ? -180 + 75 : 360 - 75;
		angle %= 360;

		PoweredShaftBlockEntity currentShaft = getShaft();
		if (currentShaft == null || currentShaft.getSpeed() == 0)
			return;

		if (angle >= 0 && !(prevAngle > 180 && angle < 180)) {
			prevAngle = angle;
			return;
		}
		if (angle < 0 && !(prevAngle < -180 && angle > -180)) {
			prevAngle = angle;
			return;
		}

		FluidTankBlockEntity sourceBE = source.get();
		if (sourceBE != null) {
			FluidTankBlockEntity controller = sourceBE.getControllerBE();
			if (controller != null && controller.boiler != null) {
				controller.boiler.queueSoundOnSide(worldPosition, SteamEngineBlock.getFacing(getBlockState()));
			}
		}

		Direction facing = SteamEngineBlock.getFacing(getBlockState());

		Vec3 offset = VecHelper.rotate(new Vec3(0, 0, 1).add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
			.multiply(1, 1, 0)
			.normalize()
			.scale(.5f)), AngleHelper.verticalAngle(facing), Axis.X);
		offset = VecHelper.rotate(offset, AngleHelper.horizontalAngle(facing), Axis.Y);
		Vec3 v = offset.scale(.5f)
			.add(Vec3.atCenterOf(worldPosition));
		Vec3 m = offset.subtract(Vec3.atLowerCornerOf(facing.getNormal())
			.scale(.75f));
		level.addParticle(new SteamJetParticleData(1), v.x, v.y, v.z, m.x, m.y, m.z);

		prevAngle = angle;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Float getTargetAngle() {
		float angle = 0;
		BlockState blockState = getBlockState();
		if (!AllBlocks.STEAM_ENGINE.has(blockState))
			return null;

		Direction facing = SteamEngineBlock.getFacing(blockState);
		PoweredShaftBlockEntity shaft = getShaft();
		Axis facingAxis = facing.getAxis();
		Axis axis = Axis.Y;

		if (shaft == null)
			return null;

		BlockState shaftState = shaft.getBlockState();
		if (shaftState.getBlock() instanceof IRotate ir)
			axis = ir.getRotationAxis(shaftState);
		float time = AnimationTickHolder.getRenderTime(shaft.getLevel());
		float offset = shaft.getRotationAngleOffset(axis);
		angle = ((time * shaft.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;

		if (axis == facingAxis)
			return null;
		if (axis.isHorizontal() && (facingAxis == Axis.X ^ facing.getAxisDirection() == AxisDirection.POSITIVE))
			angle *= -1;
		if (axis == Axis.X && facing == Direction.DOWN)
			angle *= -1;
		return angle;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		PoweredShaftBlockEntity shaft = getShaft();
		return shaft != null && shaft.addToEngineTooltip(tooltip, isPlayerSneaking);
	}

}

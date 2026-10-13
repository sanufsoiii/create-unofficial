package com.simibubi.create.content.kinetics.base;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class KineticBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {

	public @Nullable Long network;
	public @Nullable BlockPos source;
	public boolean networkDirty;
	public boolean updateSpeed;
	public int preventSpeedUpdate;

	protected float speed;
	protected float capacity;
	protected float stress;
	protected boolean overStressed;

	public KineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getTheoreticalSpeed() {
		return speed;
	}

	public boolean hasNetwork() {
		return network != null;
	}

	public static void switchToBlockState(Level world, BlockPos pos, BlockState state) {
		if (world.isClientSide)
			return;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		BlockState currentState = world.getBlockState(pos);
		if (currentState == state)
			return;

		world.setBlock(pos, state, Block.UPDATE_ALL);
	}

	public int getRotationAngleOffset(Axis axis) {
		return 0;
	}

	public boolean hasSource() {
		return source != null;
	}

	public void removeSource() {
		source = null;
	}

	public void detachKinetics() {
	}

	public void attachKinetics() {
	}

	public static float convertToDirection(float axisSpeed, net.minecraft.core.Direction d) {
		return IRotate.convertToDirection(axisSpeed, d);
	}

	public static float convertToAngular(float speed) {
		return IRotate.convertToAngular(speed);
	}
}

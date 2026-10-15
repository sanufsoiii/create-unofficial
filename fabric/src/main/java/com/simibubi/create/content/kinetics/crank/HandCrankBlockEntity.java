package com.simibubi.create.content.kinetics.crank;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HandCrankBlockEntity extends GeneratingKineticBlockEntity {

	public int inUse;
	public boolean backwards;
	public float independentAngle;
	public float chasingAngularVelocity;

	public HandCrankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void turn(boolean back) {
		boolean update = false;

		if (getGeneratedSpeed() == 0 || back != backwards)
			update = true;

		inUse = 10;
		this.backwards = back;
		if (update && level != null && !level.isClientSide)
			updateGeneratedRotation();
	}

	public float getIndependentAngle(float partialTicks) {
		return independentAngle + partialTicks * chasingAngularVelocity;
	}

	@Override
	public float getGeneratedSpeed() {
		Block block = getBlockState().getBlock();
		if (!(block instanceof HandCrankBlock crank))
			return 0;
		int speed = (inUse == 0 ? 0 : clockwise() ? -1 : 1) * crank.getRotationSpeed();
		return convertToDirection(speed, getBlockState().getValue(HandCrankBlock.FACING));
	}

	protected boolean clockwise() {
		return backwards;
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("InUse", inUse);
		compound.putBoolean("Backwards", backwards);
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		inUse = compound.getInt("InUse");
		backwards = compound.getBoolean("Backwards");
		super.read(compound, registries, clientPacket);
	}

	public void tick() {
		float actualAngularSpeed = KineticBlockEntity.convertToAngular(getSpeed());
		chasingAngularVelocity += (actualAngularSpeed - chasingAngularVelocity) / 4f;
		independentAngle += chasingAngularVelocity;

		if (inUse > 0) {
			inUse--;

			if (inUse == 0 && level != null && !level.isClientSide) {
				updateGeneratedRotation();
			}
		}
	}

	public net.createmod.catnip.render.SuperByteBuffer getRenderedHandle() {
		BlockState blockState = getBlockState();
		net.minecraft.core.Direction facing = blockState.getOptionalValue(HandCrankBlock.FACING)
			.orElse(net.minecraft.core.Direction.UP);
		return net.createmod.catnip.render.CachedBuffers.partialFacing(com.simibubi.create.AllPartialModels.HAND_CRANK_HANDLE, blockState, facing.getOpposite());
	}

	public boolean shouldRenderShaft() {
		return true;
	}
}

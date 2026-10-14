package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalBearingBlockEntity extends GeneratingKineticBlockEntity
	implements IBearingBlockEntity, IDisplayAssemblyExceptions {

	public float angle;
	public boolean running;
	public boolean assembleNextTick;
	public float clientAngleDiff;
	public AssemblyException lastException;

	public MechanicalBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean isWoodenTop() {
		return false;
	}

	@Override
	public float getInterpolatedAngle(float partialTicks) {
		return angle;
	}

	@Override
	public void setAngle(float forcedAngle) {
		angle = forcedAngle;
	}

	public void disassemble() {
		if (!running)
			return;
		running = false;
		sendData();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isWindmill() {
		return false;
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		angle = compound.getFloat("Angle");
		running = compound.getBoolean("Running");
		lastException = AssemblyException.read(compound, registries);
		super.read(compound, registries, clientPacket);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putFloat("Angle", angle);
		compound.putBoolean("Running", running);
		AssemblyException.write(compound, registries, lastException);
		super.write(compound, registries, clientPacket);
	}
}

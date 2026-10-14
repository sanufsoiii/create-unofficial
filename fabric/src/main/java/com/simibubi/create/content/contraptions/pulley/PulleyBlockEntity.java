package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PulleyBlockEntity extends KineticBlockEntity implements ThresholdSwitchObservable, IDisplayAssemblyExceptions {

	public int initialOffset;
	public boolean assembleNextTick;
	public boolean running;
	public float offset;
	public AssemblyException lastException;
	protected float prevAnimatedOffset;

	public PulleyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void onLengthBroken() {
		offset = 0;
		sendData();
	}

	@Override
	public int getCurrentValue() {
		return worldPosition.getY() - (int) offset;
	}

	@Override
	public int getMinValue() {
		return level != null ? level.getMinBuildHeight() : -64;
	}

	@Override
	public int getMaxValue() {
		return worldPosition.getY();
	}

	@Override
	public MutableComponent format(int value) {
		return CreateLang.translateDirect("gui.threshold_switch.pulley_y_level", value);
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		initialOffset = compound.getInt("InitialOffset");
		offset = compound.getFloat("Offset");
		running = compound.getBoolean("Running");
		lastException = AssemblyException.read(compound, registries);
		super.read(compound, registries, clientPacket);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("InitialOffset", initialOffset);
		compound.putFloat("Offset", offset);
		compound.putBoolean("Running", running);
		AssemblyException.write(compound, registries, lastException);
		super.write(compound, registries, clientPacket);
	}
}

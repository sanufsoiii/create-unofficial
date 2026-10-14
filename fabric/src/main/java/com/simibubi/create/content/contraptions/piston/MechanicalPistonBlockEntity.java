package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalPistonBlockEntity extends KineticBlockEntity implements IDisplayAssemblyExceptions {

	public boolean assembleNextTick;
	public AssemblyException lastException;
	public boolean running;
	public float offset;
	protected int extensionLength;
	protected float clientOffsetDiff;

	public MechanicalPistonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		extensionLength = compound.getInt("ExtensionLength");
		lastException = AssemblyException.read(compound, registries);
		super.read(compound, registries, clientPacket);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		tag.putInt("ExtensionLength", extensionLength);
		AssemblyException.write(tag, registries, lastException);
		super.write(tag, registries, clientPacket);
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	public int getExtensionRange() {
		return extensionLength;
	}
}

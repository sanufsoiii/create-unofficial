package com.simibubi.create.content.kinetics.chainConveyor;

import java.util.HashSet;
import java.util.Set;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChainConveyorBlockEntity extends KineticBlockEntity implements Clearable {

	public Set<BlockPos> connections = new HashSet<>();
	public boolean reversed;
	public boolean cancelDrops;
	public boolean checkInvalid;

	public ChainConveyorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		checkInvalid = true;
	}

	@Override
	public void clearContent() {
		connections.clear();
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		net.createmod.catnip.codecs.CatnipCodecUtils.encode(net.createmod.catnip.codecs.CatnipCodecs.set(BlockPos.CODEC), registries, connections)
			.ifPresent(t -> tag.put("Connections", t));
		tag.putBoolean("Reversed", reversed);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		connections.clear();
		net.createmod.catnip.codecs.CatnipCodecUtils.decode(net.createmod.catnip.codecs.CatnipCodecs.set(BlockPos.CODEC), registries, tag.get("Connections"))
			.ifPresent(connections::addAll);
		reversed = tag.getBoolean("Reversed");
	}

}

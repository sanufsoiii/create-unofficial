package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneRequesterBlockEntity extends StockCheckingBlockEntity {

	public boolean allowPartialRequests;
	public String encodedTargetAdress = "";
	public boolean lastRequestSucceeded;
	protected boolean redstonePowered;

	public RedstoneRequesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		allowPartialRequests = false;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putString("TargetAddress", encodedTargetAdress);
		tag.putBoolean("AllowPartial", allowPartialRequests);
		tag.putBoolean("LastSuccess", lastRequestSucceeded);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		encodedTargetAdress = tag.getString("TargetAddress");
		allowPartialRequests = tag.getBoolean("AllowPartial");
		lastRequestSucceeded = tag.getBoolean("LastSuccess");
	}

}

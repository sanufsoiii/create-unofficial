package com.simibubi.create.content.logistics.packagerLink;

import java.util.List;
import java.util.UUID;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PackagerLinkBlockEntity extends LinkWithBulbBlockEntity {

	public LogisticallyLinkedBehaviour behaviour;
	public UUID placedBy;

	public PackagerLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		setLazyTickRate(10);
		placedBy = null;
	}

	public void playEffect() {
		AllSoundEvents.STOCK_LINK.playAt(level, worldPosition, 0.75f, 1.25f, false);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(behaviour = new LogisticallyLinkedBehaviour(this, true));
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if (placedBy != null)
			tag.putUUID("PlacedBy", placedBy);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		placedBy = tag.hasUUID("PlacedBy") ? tag.getUUID("PlacedBy") : null;
	}

}

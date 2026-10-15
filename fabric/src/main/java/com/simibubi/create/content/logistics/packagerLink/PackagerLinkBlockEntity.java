package com.simibubi.create.content.logistics.packagerLink;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.apache.commons.lang3.mutable.MutableBoolean;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
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

	public InventorySummary fetchSummaryFromPackager(@Nullable IdentifiedInventory ignoredHandler) {
		PackagerBlockEntity packager = getPackager();
		if (packager == null)
			return InventorySummary.EMPTY;
		return packager.getAvailableItems();
	}

	public Pair<PackagerBlockEntity, PackagingRequest> processRequest(ItemStack stack, int amount, String address,
		int linkIndex, MutableBoolean finalLink, int orderId, @Nullable PackageOrderWithCrafts context,
		@Nullable IdentifiedInventory ignoredHandler) {
		PackagerBlockEntity packager = getPackager();
		if (packager == null)
			return null;

		InventorySummary summary = packager.getAvailableItems();
		int availableCount = summary.getCountOf(stack);
		if (availableCount == 0)
			return null;
		int toWithdraw = Math.min(amount, availableCount);
		return Pair.of(packager,
			PackagingRequest.create(stack, toWithdraw, address, linkIndex, finalLink, 0, orderId, context));
	}

	@Nullable
	public PackagerBlockEntity getPackager() {
		BlockState blockState = getBlockState();
		if (behaviour != null && behaviour.redstonePower == 15)
			return null;
		BlockPos source = worldPosition.relative(PackagerLinkBlock.getConnectedDirection(blockState)
			.getOpposite());
		if (!(level.getBlockEntity(source) instanceof PackagerBlockEntity packager))
			return null;
		return packager;
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

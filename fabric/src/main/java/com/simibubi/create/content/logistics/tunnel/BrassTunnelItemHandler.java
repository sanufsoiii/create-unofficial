package com.simibubi.create.content.logistics.tunnel;

import java.util.Collections;
import java.util.Iterator;

import com.simibubi.create.foundation.item.ItemHelper;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public class BrassTunnelItemHandler implements SingleSlotStorage<ItemVariant> {

	private final BrassTunnelBlockEntity blockEntity;

	public BrassTunnelItemHandler(BrassTunnelBlockEntity be) {
		this.blockEntity = be;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!blockEntity.hasDistributionBehaviour()) {
			Storage<ItemVariant> beltCapability = blockEntity.getBeltCapability();
			if (beltCapability == null)
				return 0;
			return beltCapability.insert(resource, maxAmount, transaction);
		}

		if (!blockEntity.canTakeItems())
			return 0;

		ItemStack stack = resource.toStack(ItemHelper.truncateLong(maxAmount));
		long inserted = Math.min(stack.getCount(), stack.getMaxStackSize());
		if (inserted <= 0)
			return 0;

		transaction.addCloseCallback((t, result) -> {
			if (result.wasCommitted()) {
				ItemStack toDistribute = resource.toStack((int) inserted);
				blockEntity.setStackToDistribute(toDistribute, null);
			}
		});
		return inserted;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		Storage<ItemVariant> beltCapability = blockEntity.getBeltCapability();
		if (beltCapability == null)
			return 0;
		return beltCapability.extract(resource, maxAmount, transaction);
	}

	@Override
	public boolean isResourceBlank() {
		return blockEntity.getStackToDistribute().isEmpty();
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(blockEntity.getStackToDistribute());
	}

	@Override
	public long getAmount() {
		return blockEntity.getStackToDistribute().getCount();
	}

	@Override
	public long getCapacity() {
		ItemStack stack = blockEntity.getStackToDistribute();
		return stack.isEmpty() ? 64 : stack.getMaxStackSize();
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return Collections.<StorageView<ItemVariant>>singletonList(this).iterator();
	}
}

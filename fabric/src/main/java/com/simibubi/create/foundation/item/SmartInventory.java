package com.simibubi.create.foundation.item;

import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class SmartInventory extends ItemStackHandlerContainer {

	protected boolean extractionAllowed;
	protected boolean insertionAllowed;
	protected boolean stackNonStackables;
	protected int stackSize;
	private SyncedBlockEntity blockEntity;
	private Runnable updateCallback;

	public SmartInventory(int slots, SyncedBlockEntity be) {
		this(slots, be, 64, false);
	}

	public SmartInventory(int slots, SyncedBlockEntity be, int stackSize, boolean stackNonStackables) {
		super(slots);
		this.stackNonStackables = stackNonStackables;
		this.insertionAllowed = true;
		this.extractionAllowed = true;
		this.stackSize = stackSize;
		this.blockEntity = be;
	}

	public SmartInventory withMaxStackSize(int maxStackSize) {
		this.stackSize = maxStackSize;
		return this;
	}

	public SmartInventory whenContentsChanged(Runnable updateCallback) {
		this.updateCallback = updateCallback;
		return this;
	}

	public SmartInventory allowInsertion() {
		this.insertionAllowed = true;
		return this;
	}

	public SmartInventory allowExtraction() {
		this.extractionAllowed = true;
		return this;
	}

	public SmartInventory forbidInsertion() {
		this.insertionAllowed = false;
		return this;
	}

	public SmartInventory forbidExtraction() {
		this.extractionAllowed = false;
		return this;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!insertionAllowed)
			return 0;
		return super.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!extractionAllowed)
			return 0;
		if (stackNonStackables) {
			try (Transaction t = transaction.openNested()) {
				long extracted = super.extract(resource, maxAmount, t);
				t.abort();
				if (extracted != 0 && resource.getItem().getDefaultMaxStackSize() < extracted)
					maxAmount = resource.getItem().getDefaultMaxStackSize();
			}
		}
		return super.extract(resource, maxAmount, transaction);
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		if (updateCallback != null)
			updateCallback.run();
		if (blockEntity != null)
			blockEntity.notifyUpdate();
	}

	@Override
	public int getSlotLimit(int slot) {
		return Math.min(stackNonStackables ? 64 : super.getSlotLimit(slot), stackSize);
	}

	public void deserializeNBT(CompoundTag compound) {
		deserializeNBT(null, compound);
	}

	public CompoundTag serializeNBT() {
		return serializeNBT((HolderLookup.Provider) null);
	}

	public void clear() {
		for (int i = 0; i < getSlotCount(); i++) {
			setStackInSlot(i, net.minecraft.world.item.ItemStack.EMPTY);
		}
	}
}

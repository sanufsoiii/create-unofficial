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
	private java.util.function.Consumer<Integer> slotCallback;

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

	public SmartInventory whenContentsChanged(java.util.function.Consumer<Integer> slotCallback) {
		this.slotCallback = slotCallback;
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
		if (slotCallback != null)
			slotCallback.accept(slot);
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

	public net.minecraft.world.item.ItemStack insertItem(int slot, net.minecraft.world.item.ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return net.minecraft.world.item.ItemStack.EMPTY;
		if (!insertionAllowed)
			return stack;
		net.minecraft.world.item.ItemStack existing = getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
		if (!existing.isEmpty()) {
			if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, existing))
				return stack;
			limit -= existing.getCount();
		}
		if (limit <= 0)
			return stack;
		boolean reachedLimit = stack.getCount() > limit;
		if (!simulate) {
			if (existing.isEmpty()) {
				setStackInSlot(slot, reachedLimit ? stack.copyWithCount(limit) : stack.copy());
			} else {
				existing.grow(reachedLimit ? limit : stack.getCount());
				onContentsChanged(slot);
			}
		}
		return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : net.minecraft.world.item.ItemStack.EMPTY;
	}

	public net.minecraft.world.item.ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0 || !extractionAllowed)
			return net.minecraft.world.item.ItemStack.EMPTY;
		net.minecraft.world.item.ItemStack existing = getStackInSlot(slot);
		if (existing.isEmpty())
			return net.minecraft.world.item.ItemStack.EMPTY;
		int toExtract = Math.min(amount, existing.getCount());
		if (existing.getCount() <= amount) {
			if (!simulate) {
				setStackInSlot(slot, net.minecraft.world.item.ItemStack.EMPTY);
			}
			return simulate ? existing.copy() : existing;
		} else {
			if (!simulate) {
				setStackInSlot(slot, existing.copyWithCount(existing.getCount() - toExtract));
			}
			return existing.copyWithCount(toExtract);
		}
	}
}

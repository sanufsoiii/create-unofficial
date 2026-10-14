package com.simibubi.create.content.logistics.packager;

import com.simibubi.create.content.logistics.box.PackageItem;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

public class PackagerItemHandler extends ItemStackHandler {

	private final PackagerBlockEntity blockEntity;

	public PackagerItemHandler(PackagerBlockEntity blockEntity) {
		super(1);
		this.blockEntity = blockEntity;
	}

	@Override
	public int getSlotCount() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return blockEntity.heldBox;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot != 0)
			return;
		blockEntity.heldBox = stack;
		blockEntity.notifyUpdate();
	}

	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (!blockEntity.heldBox.isEmpty() || !blockEntity.queuedExitingPackages.isEmpty())
			return stack;
		if (!isItemValid(slot, stack))
			return stack;
		if (!blockEntity.unwrapBox(stack, true))
			return stack;
		if (!simulate) {
			blockEntity.unwrapBox(stack, false);
			blockEntity.triggerStockCheck();
		}
		return stack.copyWithCount(stack.getCount() - 1);
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (blockEntity.animationTicks != 0)
			return ItemStack.EMPTY;
		ItemStack box = blockEntity.heldBox;
		if (!simulate)
			setStackInSlot(slot, ItemStack.EMPTY);
		return box;
	}

	public int getSlotLimit(int slot) {
		return 1;
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		return PackageItem.isPackage(stack);
	}
}

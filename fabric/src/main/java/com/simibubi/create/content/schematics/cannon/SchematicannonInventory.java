package com.simibubi.create.content.schematics.cannon;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerSlot;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SchematicannonInventory extends ItemStackHandler {
	private final SchematicannonBlockEntity blockEntity;

	public SchematicannonInventory(SchematicannonBlockEntity blockEntity) {
		super(5);
		this.blockEntity = blockEntity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		blockEntity.setChanged();
	}

	public boolean isItemValid(int slot, ItemStack stack) {
		switch (slot) {
		case 0: // Blueprint Slot
			return AllItems.SCHEMATIC.isIn(stack);
		case 1: // Blueprint output
			return false;
		case 2: // Book input
			return AllBlocks.CLIPBOARD.isIn(stack) || stack.is(Items.BOOK)
				|| stack.is(Items.WRITTEN_BOOK);
		case 3: // Material List output
			return false;
		case 4: // Gunpowder
			return stack.is(Items.GUNPOWDER);
		default:
			return true;
		}
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;
		if (slot < 0 || slot >= getSlotCount())
			return ItemStack.EMPTY;
		ItemStack existing = getStackInSlot(slot);
		if (existing.isEmpty())
			return ItemStack.EMPTY;
		int toExtract = Math.min(amount, existing.getCount());
		if (simulate) {
			ItemStack result = existing.copy();
			result.setCount(toExtract);
			return result;
		} else {
			ItemStack result = existing.split(toExtract);
			if (existing.isEmpty()) {
				setStackInSlot(slot, ItemStack.EMPTY);
			} else {
				setStackInSlot(slot, existing);
			}
			onContentsChanged(slot);
			return result;
		}
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant variant, int count) {
		return isItemValid(slot, variant.toStack(count));
	}

	@Override
	protected ItemStackHandlerSlot makeSlot(int slot, ItemStack stack) {
		return new ItemStackHandlerSlot(slot, this, stack) {
			@Override
			protected boolean canInsert(ItemVariant variant) {
				return isItemValid(slot, variant.toStack());
			}
		};
	}
}

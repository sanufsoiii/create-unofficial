package com.simibubi.create.foundation.recipe;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class RecipeWrapper implements RecipeInput {
	protected final ItemStackHandler inv;

	public RecipeWrapper(ItemStackHandler inv) {
		this.inv = inv;
	}

	@Override
	public int size() {
		return inv.getSlotCount();
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getStackInSlot(slot);
	}

	@Override
	public boolean isEmpty() {
		return inv.empty();
	}

	public ItemStackHandler getHandler() {
		return inv;
	}
}

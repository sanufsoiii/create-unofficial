package com.simibubi.create.content.equipment.toolbox;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandler;
import net.minecraft.world.item.ItemStack;

public class ToolboxSlot extends SlotItemHandler {

	private final ToolboxMenu toolboxMenu;
	private final ToolboxInventory inventory;
	private final int slotIndex;
	private final boolean isVisible;

	public ToolboxSlot(ToolboxMenu menu, ToolboxInventory itemHandler, int index, int xPosition, int yPosition, boolean isVisible) {
		super(itemHandler, index, xPosition, yPosition);
		this.toolboxMenu = menu;
		this.inventory = itemHandler;
		this.slotIndex = index;
		this.isVisible = isVisible;
	}

	@Override
	public boolean isActive() {
		return !toolboxMenu.renderPass && super.isActive() && isVisible;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		ItemStack maxAdd = stack.copy();
		int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);

		ItemStack currentStack = inventory.getStackInSlot(slotIndex);
		ItemStack remainder = inventory.insertItem(slotIndex, maxAdd, true);
		int current = currentStack.getCount();
		int added = maxInput - remainder.getCount();
		return current + added;
	}

}

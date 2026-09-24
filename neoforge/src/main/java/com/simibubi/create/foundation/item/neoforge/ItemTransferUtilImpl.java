package com.simibubi.create.foundation.item.neoforge;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ItemTransferUtilImpl {

    public static ItemStack insertItem(Object target, ItemStack stack, boolean simulate) {
        if (target instanceof IItemHandler itemHandler) {
            return ItemHandlerHelper.insertItem(itemHandler, stack, simulate);
        } else if (target instanceof Container container) {
            return ItemHandlerHelper.insertItem(new InvWrapper(container), stack, simulate);
        }
        return stack;
    }

    public static ItemStack extractItem(Object target, int slot, int amount, boolean simulate) {
        if (target instanceof IItemHandler itemHandler) {
            return itemHandler.extractItem(slot, amount, simulate);
        } else if (target instanceof Container container) {
            return new InvWrapper(container).extractItem(slot, amount, simulate);
        }
        return ItemStack.EMPTY;
    }
}

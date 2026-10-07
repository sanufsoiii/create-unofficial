package com.simibubi.create.foundation.item.neoforge;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ItemTransferUtilImpl {

    public static ItemStack insertItem(Object target, ItemStack stack, boolean simulate) {
        if (target instanceof IItemHandler itemHandler) {
            return ItemHandlerHelper.insertItemStacked(itemHandler, stack, simulate);
        } else if (target instanceof Container container) {
            return ItemHandlerHelper.insertItemStacked(new InvWrapper(container), stack, simulate);
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

    public static Object getInventory(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side) {
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, side);
    }

    public static ItemStack extract(Object target, java.util.function.Predicate<ItemStack> test, com.simibubi.create.foundation.item.CommonItemHelper.ExtractionCountMode mode, int amount, boolean simulate) {
        if (target instanceof IItemHandler itemHandler) {
            return com.simibubi.create.foundation.item.ItemHelper.extract(itemHandler, test, mode, amount, simulate);
        } else if (target instanceof Container container) {
            return com.simibubi.create.foundation.item.CommonItemHelper.extract(container, test, mode, amount, simulate);
        }
        return ItemStack.EMPTY;
    }
}

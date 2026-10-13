package com.simibubi.create.foundation.item;

import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Platform-independent item transfer utilities for inventories and contraptions.
 * Connects to NeoForge IItemHandler on NeoForge and Fabric Transfer API on Fabric.
 */
public final class ItemTransferUtil {

    private ItemTransferUtil() {}

    /**
     * Inserts an item stack into an inventory or handler object.
     *
     * @param target      target container / storage object
     * @param stack       item stack to insert
     * @param simulate    whether to simulate insertion
     * @return remaining item stack that could not be inserted
     */
    public static ItemStack insertItem(Object target, ItemStack stack, boolean simulate) {
        return (ItemStack) com.simibubi.create.foundation.utility.PlatformDispatch.call(
            "com.simibubi.create.foundation.item.neoforge.ItemTransferUtilImpl",
            "com.simibubi.create.foundation.item.fabric.ItemTransferUtilImpl",
            "insertItem",
            new Class<?>[]{Object.class, ItemStack.class, boolean.class},
            target, stack, simulate
        );
    }

    /**
     * Extracts an item stack from an inventory or handler object.
     */
    public static ItemStack extractItem(Object target, int slot, int amount, boolean simulate) {
        return (ItemStack) com.simibubi.create.foundation.utility.PlatformDispatch.call(
            "com.simibubi.create.foundation.item.neoforge.ItemTransferUtilImpl",
            "com.simibubi.create.foundation.item.fabric.ItemTransferUtilImpl",
            "extractItem",
            new Class<?>[]{Object.class, int.class, int.class, boolean.class},
            target, slot, amount, simulate
        );
    }

    /**
     * Gets the inventory / item storage capability or API lookup at the given position and side.
     */
    public static Object getInventory(Level level, BlockPos pos, @Nullable Direction side) {
        return com.simibubi.create.foundation.utility.PlatformDispatch.call(
            "com.simibubi.create.foundation.item.neoforge.ItemTransferUtilImpl",
            "com.simibubi.create.foundation.item.fabric.ItemTransferUtilImpl",
            "getInventory",
            new Class<?>[]{Level.class, BlockPos.class, Direction.class},
            level, pos, side
        );
    }

    /**
     * Extracts items matching the given predicate from the inventory or handler object.
     */
    public static ItemStack extract(Object target, Predicate<ItemStack> test, CommonItemHelper.ExtractionCountMode mode, int amount, boolean simulate) {
        return (ItemStack) com.simibubi.create.foundation.utility.PlatformDispatch.call(
            "com.simibubi.create.foundation.item.neoforge.ItemTransferUtilImpl",
            "com.simibubi.create.foundation.item.fabric.ItemTransferUtilImpl",
            "extract",
            new Class<?>[]{Object.class, Predicate.class, CommonItemHelper.ExtractionCountMode.class, int.class, boolean.class},
            target, test, mode, amount, simulate
        );
    }
}

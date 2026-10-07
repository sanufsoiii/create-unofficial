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
    @ExpectPlatform
    public static ItemStack insertItem(Object target, ItemStack stack, boolean simulate) {
        throw new AssertionError("@ExpectPlatform");
    }

    /**
     * Extracts an item stack from an inventory or handler object.
     */
    @ExpectPlatform
    public static ItemStack extractItem(Object target, int slot, int amount, boolean simulate) {
        throw new AssertionError("@ExpectPlatform");
    }

    /**
     * Gets the inventory / item storage capability or API lookup at the given position and side.
     */
    @ExpectPlatform
    public static Object getInventory(Level level, BlockPos pos, @Nullable Direction side) {
        throw new AssertionError("@ExpectPlatform");
    }

    /**
     * Extracts items matching the given predicate from the inventory or handler object.
     */
    @ExpectPlatform
    public static ItemStack extract(Object target, Predicate<ItemStack> test, CommonItemHelper.ExtractionCountMode mode, int amount, boolean simulate) {
        throw new AssertionError("@ExpectPlatform");
    }
}

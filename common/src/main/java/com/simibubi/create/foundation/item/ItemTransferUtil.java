package com.simibubi.create.foundation.item;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

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
}

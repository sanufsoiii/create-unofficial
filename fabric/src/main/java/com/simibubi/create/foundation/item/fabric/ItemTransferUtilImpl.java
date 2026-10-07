package com.simibubi.create.foundation.item.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ItemTransferUtilImpl {

    @SuppressWarnings("unchecked")
    public static ItemStack insertItem(Object target, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        if (target instanceof Storage<?> storage) {
            Storage<ItemVariant> itemStorage = (Storage<ItemVariant>) storage;
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = itemStorage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
                if (!simulate) {
                    transaction.commit();
                }
                int remainderCount = (int) (stack.getCount() - inserted);
                if (remainderCount <= 0) return ItemStack.EMPTY;
                return stack.copyWithCount(remainderCount);
            }
        } else if (target instanceof Container container) {
            // Vanilla container slot-based fallback
            ItemStack copy = stack.copy();
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack slotStack = container.getItem(i);
                if (slotStack.isEmpty()) {
                    if (!simulate) container.setItem(i, copy.copy());
                    return ItemStack.EMPTY;
                } else if (ItemStack.isSameItemSameComponents(slotStack, copy)) {
                    int max = Math.min(container.getMaxStackSize(), slotStack.getMaxStackSize());
                    int canAdd = max - slotStack.getCount();
                    if (canAdd > 0) {
                        int toAdd = Math.min(canAdd, copy.getCount());
                        if (!simulate) slotStack.grow(toAdd);
                        copy.shrink(toAdd);
                        if (copy.isEmpty()) return ItemStack.EMPTY;
                    }
                }
            }
            return copy;
        }

        return stack;
    }

    public static ItemStack extractItem(Object target, int slot, int amount, boolean simulate) {
        if (target instanceof Container container) {
            if (slot < 0 || slot >= container.getContainerSize()) return ItemStack.EMPTY;
            ItemStack current = container.getItem(slot);
            if (current.isEmpty()) return ItemStack.EMPTY;
            int toExtract = Math.min(amount, current.getCount());
            ItemStack result = current.copyWithCount(toExtract);
            if (!simulate) {
                current.shrink(toExtract);
                container.setChanged();
            }
            return result;
        }
        return ItemStack.EMPTY;
    }

    public static Object getInventory(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side) {
        return net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level, pos, side);
    }

    @SuppressWarnings("unchecked")
    public static ItemStack extract(Object target, java.util.function.Predicate<ItemStack> test, com.simibubi.create.foundation.item.CommonItemHelper.ExtractionCountMode mode, int amount, boolean simulate) {
        if (target instanceof Container container) {
            return com.simibubi.create.foundation.item.CommonItemHelper.extract(container, test, mode, amount, simulate);
        } else if (target instanceof Storage<?> storage) {
            Storage<ItemVariant> itemStorage = (Storage<ItemVariant>) storage;
            boolean amountRequired = mode == com.simibubi.create.foundation.item.CommonItemHelper.ExtractionCountMode.EXACTLY;
            ItemStack extracting = ItemStack.EMPTY;

            try (Transaction transaction = Transaction.openOuter()) {
                for (net.fabricmc.fabric.api.transfer.v1.storage.StorageView<ItemVariant> view : itemStorage) {
                    if (view.isResourceBlank()) continue;
                    ItemVariant variant = view.getResource();
                    ItemStack stack = variant.toStack();
                    if (!test.test(stack)) continue;
                    if (!extracting.isEmpty() && !com.simibubi.create.foundation.item.CommonItemHelper.canItemStackAmountsStack(stack, extracting)) continue;

                    int maxToExtract = amount - extracting.getCount();
                    if (maxToExtract <= 0) break;

                    long extracted = view.extract(variant, maxToExtract, transaction);
                    if (extracted <= 0) continue;

                    if (extracting.isEmpty()) {
                        extracting = variant.toStack((int) extracted);
                    } else {
                        extracting.grow((int) extracted);
                    }

                    if (extracting.getCount() >= amount) break;
                }

                if (amountRequired && extracting.getCount() < amount) {
                    return ItemStack.EMPTY;
                }

                if (!simulate) {
                    transaction.commit();
                }
                return extracting;
            }
        }
        return ItemStack.EMPTY;
    }
}

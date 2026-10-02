package com.simibubi.create.foundation.recipe;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public interface SupportsItemCopying {
	default ItemStack createCopy(ItemStack original, int count) {
		ItemStack copyWithCount = original.copyWithCount(count);
		copyWithCount.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		copyWithCount.remove(DataComponents.STORED_ENCHANTMENTS);
		return copyWithCount;
	}

	default boolean canCopyFromItem(ItemStack item) {
		return item.has(getComponentType());
	}

	default boolean canCopyToItem(ItemStack item) {
		return !item.has(getComponentType());
	}

	DataComponentType<?> getComponentType();
}

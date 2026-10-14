package com.simibubi.create;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class AllEnchantments {
	public static final ResourceKey<Enchantment>
			POTATO_RECOVERY = key("potato_recovery"),
			CAPACITY = key("capacity");

	private static ResourceKey<Enchantment> key(String name) {
		return ResourceKey.create(Registries.ENCHANTMENT, Create.asResource(name));
	}
}

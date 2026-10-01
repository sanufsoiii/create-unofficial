package com.simibubi.create.content.logistics.item.filter.attribute.fabric;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ItemAttributeImpl {
	public static Registry<ItemAttributeType> getItemAttributeTypeRegistry() {
		return null; // Will hook into Fabric CreateBuiltInRegistries when ready
	}

	public static ResourceKey<Registry<ItemAttributeType>> getItemAttributeTypeRegistryKey() {
		return null;
	}
}

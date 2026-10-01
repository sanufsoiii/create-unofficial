package com.simibubi.create.content.logistics.item.filter.attribute.neoforge;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ItemAttributeImpl {
	public static Registry<ItemAttributeType> getItemAttributeTypeRegistry() {
		return CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE;
	}

	public static ResourceKey<Registry<ItemAttributeType>> getItemAttributeTypeRegistryKey() {
		return CreateRegistries.ITEM_ATTRIBUTE_TYPE;
	}
}

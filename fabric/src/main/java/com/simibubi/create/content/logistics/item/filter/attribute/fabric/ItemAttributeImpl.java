package com.simibubi.create.content.logistics.item.filter.attribute.fabric;

import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ItemAttributeImpl {
	public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_TYPE =
		ResourceKey.createRegistryKey(com.simibubi.create.Create.asResource("item_attribute_type"));

	public static final Registry<ItemAttributeType> REGISTRY =
		com.simibubi.create.api.registry.CreateRegistryHelper.register(ITEM_ATTRIBUTE_TYPE, false, () -> {});

	public static Registry<ItemAttributeType> getItemAttributeTypeRegistry() {
		return REGISTRY;
	}

	public static ResourceKey<Registry<ItemAttributeType>> getItemAttributeTypeRegistryKey() {
		return ITEM_ATTRIBUTE_TYPE;
	}
}

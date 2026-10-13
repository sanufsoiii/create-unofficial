package com.simibubi.create.api.registry;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Keys for registries added by Create on Fabric.
 */
public class CreateRegistries {
	public static final ResourceKey<Registry<FanProcessingType>> FAN_PROCESSING_TYPE = key("fan_processing_type");

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(Create.asResource(name));
	}

	private CreateRegistries() {
		throw new AssertionError("This class should not be instantiated");
	}
}

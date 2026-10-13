package com.simibubi.create.api.registry;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;

import net.minecraft.core.Registry;

/**
 * Built-in static registries for Create on Fabric.
 */
public class CreateBuiltInRegistries {
	public static final Registry<FanProcessingType> FAN_PROCESSING_TYPE =
		CreateRegistryHelper.simpleWithFreezeCallback(CreateRegistries.FAN_PROCESSING_TYPE, FanProcessingTypeRegistry::init);

	@Internal
	public static void init() {
		// triggers classloading and registration
	}

	private CreateBuiltInRegistries() {
		throw new AssertionError("This class should not be instantiated");
	}
}

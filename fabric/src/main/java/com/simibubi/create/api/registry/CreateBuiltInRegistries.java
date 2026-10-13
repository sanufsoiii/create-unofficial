package com.simibubi.create.api.registry;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingTypeRegistry;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;

import net.minecraft.core.Registry;

/**
 * Built-in static registries for Create on Fabric.
 */
public class CreateBuiltInRegistries {
	public static final Registry<FanProcessingType> FAN_PROCESSING_TYPE =
		CreateRegistryHelper.simpleWithFreezeCallback(CreateRegistries.FAN_PROCESSING_TYPE, FanProcessingTypeRegistry::init);
	public static final Registry<ArmInteractionPointType> ARM_INTERACTION_POINT_TYPE =
		CreateRegistryHelper.simpleWithFreezeCallback(CreateRegistries.ARM_INTERACTION_POINT_TYPE, ArmInteractionPointType::init);

	@Internal
	public static void init() {
		// triggers classloading and registration
	}

	private CreateBuiltInRegistries() {
		throw new AssertionError("This class should not be instantiated");
	}
}

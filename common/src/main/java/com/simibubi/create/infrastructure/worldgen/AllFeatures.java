package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class AllFeatures {
	private static final DeferredRegister<Feature<?>> REGISTER = DeferredRegister.create(Create.ID, Registries.FEATURE);

	public static final RegistrySupplier<LayeredOreFeature> LAYERED_ORE = REGISTER.register("layered_ore", LayeredOreFeature::new);

	public static void register() {
		REGISTER.register();
	}
}

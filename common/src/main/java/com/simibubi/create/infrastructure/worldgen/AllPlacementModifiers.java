package com.simibubi.create.infrastructure.worldgen;

import com.simibubi.create.Create;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class AllPlacementModifiers {
	private static final DeferredRegister<PlacementModifierType<?>> REGISTER = DeferredRegister.create(Create.ID, Registries.PLACEMENT_MODIFIER_TYPE);

	public static final RegistrySupplier<PlacementModifierType<ConfigPlacementFilter>> CONFIG_FILTER = REGISTER.register("config_filter", () -> () -> ConfigPlacementFilter.CODEC);

	public static void register() {
		REGISTER.register();
	}
}

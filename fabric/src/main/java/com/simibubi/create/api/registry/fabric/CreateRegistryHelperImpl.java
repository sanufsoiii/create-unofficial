package com.simibubi.create.api.registry.fabric;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class CreateRegistryHelperImpl {
	private static final List<Runnable> BAKE_CALLBACKS = new ArrayList<>();

	public static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders, Runnable onBakeCallback) {
		MappedRegistry<T> mappedRegistry = new MappedRegistry<>(key, Lifecycle.stable(), hasIntrusiveHolders);
		Registry<T> registry = FabricRegistryBuilder.from(mappedRegistry)
			.attribute(RegistryAttribute.SYNCED)
			.buildAndRegister();

		if (onBakeCallback != null) {
			BAKE_CALLBACKS.add(onBakeCallback);
		}

		return registry;
	}

	public static void bakeAll() {
		BAKE_CALLBACKS.forEach(Runnable::run);
	}
}


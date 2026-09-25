package com.simibubi.create.api.registry.neoforge;

import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CreateRegistryHelperImpl {

	@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
	public static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders, Runnable onBakeCallback) {
		RegistryBuilder<T> builder = new RegistryBuilder<>(key)
			.sync(true);

		if (hasIntrusiveHolders)
			builder.withIntrusiveHolders();

		builder.onBake(r -> onBakeCallback.run());

		Registry<T> registry = builder.create();
		((WritableRegistry) BuiltInRegistries.REGISTRY)
			.register(key, registry, RegistrationInfo.BUILT_IN);
		return registry;
	}
}

package com.simibubi.create.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Cross-platform helper for registering custom vanilla-compatible built-in registries.
 */
public class CreateRegistryHelper {

	@ExpectPlatform
	public static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders, Runnable onBakeCallback) {
		throw new AssertionError();
	}

	public static <T> Registry<T> simple(ResourceKey<Registry<T>> key) {
		return register(key, false, () -> {});
	}

	public static <T> Registry<T> simpleWithFreezeCallback(ResourceKey<Registry<T>> key, Runnable onBakeCallback) {
		return register(key, false, onBakeCallback);
	}

	public static <T> Registry<T> withIntrusiveHolders(ResourceKey<Registry<T>> key) {
		return register(key, true, () -> {});
	}
}

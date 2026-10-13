package com.simibubi.create.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Cross-platform helper for registering custom vanilla-compatible built-in registries.
 */
public class CreateRegistryHelper {

	@SuppressWarnings("unchecked")
	public static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders, Runnable onBakeCallback) {
		try {
			Class<?> impl = Class.forName("com.simibubi.create.api.registry.neoforge.CreateRegistryHelperImpl");
			return (Registry<T>) impl.getMethod("register", ResourceKey.class, boolean.class, Runnable.class).invoke(null, key, hasIntrusiveHolders, onBakeCallback);
		} catch (ClassNotFoundException e) {
			try {
				Class<?> impl = Class.forName("com.simibubi.create.api.registry.fabric.CreateRegistryHelperImpl");
				return (Registry<T>) impl.getMethod("register", ResourceKey.class, boolean.class, Runnable.class).invoke(null, key, hasIntrusiveHolders, onBakeCallback);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

package com.simibubi.create.api.behaviour.movement;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;

/**
 * Public, platform-independent registry for Movement Behaviours.
 * Addons (such as Aeronautics or custom vehicle mods) can register and query
 * movement behaviours directly from the common module without platform dependencies.
 */
public final class MovementBehaviourRegistry {

	private static final Map<Block, Object> REGISTERED_BEHAVIOURS = new ConcurrentHashMap<>();

	private MovementBehaviourRegistry() {}

	/**
	 * Registers a custom movement behaviour for the given block.
	 *
	 * @param block     the target block
	 * @param behaviour the movement behaviour instance
	 * @param <T>       behaviour type
	 * @return the registered behaviour
	 */
	@SuppressWarnings("unchecked")
	public static <T> T register(@NotNull Block block, @NotNull T behaviour) {
		REGISTERED_BEHAVIOURS.put(block, behaviour);
		return behaviour;
	}

	/**
	 * Finds a registered movement behaviour for the given block.
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T get(@NotNull Block block) {
		return (T) REGISTERED_BEHAVIOURS.get(block);
	}

	/**
	 * Returns an unmodifiable view of all registered movement behaviours.
	 */
	public static Map<Block, Object> getAll() {
		return Collections.unmodifiableMap(REGISTERED_BEHAVIOURS);
	}
}

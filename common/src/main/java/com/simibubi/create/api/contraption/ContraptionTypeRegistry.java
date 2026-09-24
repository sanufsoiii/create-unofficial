package com.simibubi.create.api.contraption;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Public, platform-independent registry for Contraption types.
 * Addons (such as Aeronautics or custom vehicle mods) can register and query
 * contraption types directly from the common module without platform dependencies.
 */
public final class ContraptionTypeRegistry {

    private static final Map<ResourceLocation, Object> REGISTERED_TYPES = new ConcurrentHashMap<>();

    private ContraptionTypeRegistry() {}

    /**
     * Registers a custom contraption type with its unique identifier.
     *
     * @param id   unique ResourceLocation of the contraption type
     * @param type the contraption type object (instance of ContraptionType)
     * @param <T>  type parameter
     * @return the registered contraption type
     */
    @SuppressWarnings("unchecked")
    public static <T> T register(@NotNull ResourceLocation id, @NotNull T type) {
        REGISTERED_TYPES.put(id, type);
        return type;
    }

    /**
     * Finds a registered contraption type by ID.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T get(@NotNull ResourceLocation id) {
        return (T) REGISTERED_TYPES.get(id);
    }

    /**
     * Returns an unmodifiable view of all registered contraption types.
     */
    public static Map<ResourceLocation, Object> getAll() {
        return Collections.unmodifiableMap(REGISTERED_TYPES);
    }
}

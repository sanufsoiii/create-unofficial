package com.simibubi.create;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public class AllMapDecorationTypes {
	private static final DeferredRegister<MapDecorationType> DECORATION_TYPES = DeferredRegister.create(Create.ID, Registries.MAP_DECORATION_TYPE);

	public static final RegistrySupplier<MapDecorationType> STATION_MAP_DECORATION = DECORATION_TYPES.register("station", () -> new MapDecorationType(Create.asResource("station"), true, -1, false, true));

	public static void register() {
		DECORATION_TYPES.register();
	}
}

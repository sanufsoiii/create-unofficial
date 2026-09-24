package com.simibubi.create.impl.registry.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;

public class TagReloadHandlerImpl {
	public static void register(Runnable invalidate) {
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> invalidate.run());
	}
}

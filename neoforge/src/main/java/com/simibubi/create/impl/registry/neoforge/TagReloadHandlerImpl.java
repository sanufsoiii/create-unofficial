package com.simibubi.create.impl.registry.neoforge;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public class TagReloadHandlerImpl {
	public static void register(Runnable invalidate) {
		NeoForge.EVENT_BUS.addListener((TagsUpdatedEvent event) -> {
			if (event.shouldUpdateStaticData()) {
				invalidate.run();
			}
		});
	}
}

package com.simibubi.create.impl.registry;

import com.simibubi.create.foundation.utility.PlatformDispatch;

public class TagReloadHandler {
	public static void register(Runnable invalidate) {
		PlatformDispatch.call(
			"com.simibubi.create.impl.registry.neoforge.TagReloadHandlerImpl",
			"com.simibubi.create.impl.registry.fabric.TagReloadHandlerImpl",
			"register",
			new Class<?>[]{Runnable.class},
			invalidate
		);
	}
}

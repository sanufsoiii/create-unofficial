package com.simibubi.create.impl.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class TagReloadHandler {
	@ExpectPlatform
	public static void register(Runnable invalidate) {
		throw new AssertionError();
	}
}

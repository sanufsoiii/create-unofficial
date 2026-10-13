package com.simibubi.create.compat;

import net.fabricmc.loader.api.FabricLoader;

public class ModCheckHelperImpl {
	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}

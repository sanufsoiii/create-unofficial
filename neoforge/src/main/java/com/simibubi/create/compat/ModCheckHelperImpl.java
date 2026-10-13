package com.simibubi.create.compat;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

public class ModCheckHelperImpl {
	public static boolean isModLoaded(String modId) {
		ModList modList = ModList.get();
		if (modList != null) {
			return modList.isLoaded(modId);
		}
		LoadingModList loadingModList = LoadingModList.get();
		if (loadingModList != null) {
			return loadingModList.getModFileById(modId) != null;
		}
		return false;
	}
}

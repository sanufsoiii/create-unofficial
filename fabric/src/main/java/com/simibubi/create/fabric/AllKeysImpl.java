package com.simibubi.create.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class AllKeysImpl {
	public static boolean doesModifierAndCodeMatch(KeyMapping keyMapping, int code) {
		return code == getBoundKey(keyMapping).getValue();
	}

	public static InputConstants.Key getBoundKey(KeyMapping keyMapping) {
		return keyMapping.key;
	}
}

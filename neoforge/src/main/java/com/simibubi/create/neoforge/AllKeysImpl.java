package com.simibubi.create.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class AllKeysImpl {
	public static boolean doesModifierAndCodeMatch(KeyMapping keyMapping, int code) {
		boolean codeMatches = code == keyMapping.getKey().getValue();

		boolean modifierMatches;
		KeyModifier modifier = keyMapping.getKeyModifier();
		if (modifier == KeyModifier.NONE) {
			modifierMatches = true;
		} else {
			modifierMatches = KeyModifier.getActiveModifiers().contains(modifier);
		}

		return codeMatches && modifierMatches;
	}

	public static InputConstants.Key getBoundKey(KeyMapping keyMapping) {
		return keyMapping.getKey();
	}
}

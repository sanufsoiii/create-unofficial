package com.simibubi.create.foundation.utility;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class PlayerUtil {
	@ExpectPlatform
	public static boolean isFakePlayer(Player player) {
		throw new AssertionError("@ExpectPlatform");
	}
}

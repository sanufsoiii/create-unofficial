package com.simibubi.create.foundation.utility;

import net.minecraft.world.entity.player.Player;

public class PlayerUtil {
	public static boolean isFakePlayer(Player player) {
		return (boolean) PlatformDispatch.call(
			"com.simibubi.create.foundation.utility.PlayerUtilImpl",
			"com.simibubi.create.foundation.utility.PlayerUtilImpl",
			"isFakePlayer",
			new Class<?>[]{Player.class},
			player
		);
	}
}

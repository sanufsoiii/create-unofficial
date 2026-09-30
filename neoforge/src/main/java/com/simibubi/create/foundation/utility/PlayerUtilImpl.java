package com.simibubi.create.foundation.utility;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.FakePlayer;

public class PlayerUtilImpl {
	public static boolean isFakePlayer(Player player) {
		return player instanceof FakePlayer;
	}
}

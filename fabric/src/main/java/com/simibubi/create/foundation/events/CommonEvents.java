package com.simibubi.create.foundation.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CommonEvents {

	public static void leftClickEmpty(ServerPlayer player) {
		ItemStack stack = player.getMainHandItem();
		// TODO: Zapper support when ported
	}
}

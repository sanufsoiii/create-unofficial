package com.simibubi.create.content.equipment.tool;

import net.minecraft.class_310;
import net.minecraft.class_746;

final class KnockbackPacketClient {
	private KnockbackPacketClient() {}

	static void apply(float strength, float yRot) {
		class_746 player = class_310.method_1551().field_1724;
		if (player != null) CardboardSwordItem.knockback(player, strength, yRot);
	}
}

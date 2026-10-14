package com.simibubi.create.foundation.utility;

import net.createmod.catnip.animation.LerpedFloat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

public class ServerSpeedProvider {

	static int clientTimer = 0;
	static int serverTimer = 0;
	static boolean initialized = false;
	static LerpedFloat modifier = LerpedFloat.linear().startWithValue(1.0f);

	@Environment(EnvType.CLIENT)
	public static void clientTick() {
		if (Minecraft.getInstance().hasSingleplayerServer() && Minecraft.getInstance().isPaused())
			return;
		modifier.tickChaser();
		clientTimer++;
	}

	public static Integer getSyncInterval() {
		return 20;
	}

	public static float get() {
		return modifier.getValue();
	}
}

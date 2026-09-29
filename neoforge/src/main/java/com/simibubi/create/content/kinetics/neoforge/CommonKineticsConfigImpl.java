package com.simibubi.create.content.kinetics.neoforge;

import com.simibubi.create.infrastructure.config.AllConfigs;

public class CommonKineticsConfigImpl {

	public static float fastSpeed() {
		return AllConfigs.server().kinetics.fastSpeed.get().floatValue();
	}

	public static float mediumSpeed() {
		return AllConfigs.server().kinetics.mediumSpeed.get().floatValue();
	}

	public static boolean disableStress() {
		return AllConfigs.server().kinetics.disableStress.get();
	}

}

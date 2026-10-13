package com.simibubi.create.content.kinetics;

import com.simibubi.create.foundation.utility.PlatformDispatch;

public class CommonKineticsConfig {

	public static float fastSpeed() {
		return (float) PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.neoforge.CommonKineticsConfigImpl",
			"com.simibubi.create.content.kinetics.fabric.CommonKineticsConfigImpl",
			"fastSpeed",
			new Class<?>[0]
		);
	}

	public static float mediumSpeed() {
		return (float) PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.neoforge.CommonKineticsConfigImpl",
			"com.simibubi.create.content.kinetics.fabric.CommonKineticsConfigImpl",
			"mediumSpeed",
			new Class<?>[0]
		);
	}

	public static boolean disableStress() {
		return (boolean) PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.neoforge.CommonKineticsConfigImpl",
			"com.simibubi.create.content.kinetics.fabric.CommonKineticsConfigImpl",
			"disableStress",
			new Class<?>[0]
		);
	}

}

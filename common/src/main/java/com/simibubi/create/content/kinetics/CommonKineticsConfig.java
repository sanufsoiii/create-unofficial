package com.simibubi.create.content.kinetics;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class CommonKineticsConfig {

	@ExpectPlatform
	public static float fastSpeed() {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static float mediumSpeed() {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static boolean disableStress() {
		throw new AssertionError("@ExpectPlatform");
	}

}

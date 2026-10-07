package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SmartFluidTankHooks {

	@ExpectPlatform
	public static SmartFluidTankBehaviour.TankSegment createTankSegment(SmartFluidTankBehaviour behaviour, int tankCapacity) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static Object createHandler(SmartFluidTankBehaviour behaviour, SmartFluidTankBehaviour.TankSegment[] tanks, boolean enforceVariety) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static void invalidateCapabilities(SmartBlockEntity blockEntity) {
		throw new AssertionError("@ExpectPlatform");
	}
}

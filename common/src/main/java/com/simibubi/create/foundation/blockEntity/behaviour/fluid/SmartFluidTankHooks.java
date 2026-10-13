package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.utility.PlatformDispatch;

public class SmartFluidTankHooks {

	public static SmartFluidTankBehaviour.TankSegment createTankSegment(SmartFluidTankBehaviour behaviour, int tankCapacity) {
		return (SmartFluidTankBehaviour.TankSegment) PlatformDispatch.call(
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"createTankSegment",
			new Class<?>[]{SmartFluidTankBehaviour.class, int.class},
			behaviour, tankCapacity
		);
	}

	public static Object createHandler(SmartFluidTankBehaviour behaviour, SmartFluidTankBehaviour.TankSegment[] tanks, boolean enforceVariety) {
		return PlatformDispatch.call(
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"createHandler",
			new Class<?>[]{SmartFluidTankBehaviour.class, SmartFluidTankBehaviour.TankSegment[].class, boolean.class},
			behaviour, tanks, enforceVariety
		);
	}

	public static void invalidateCapabilities(SmartBlockEntity blockEntity) {
		PlatformDispatch.call(
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl",
			"invalidateCapabilities",
			new Class<?>[]{SmartBlockEntity.class},
			blockEntity
		);
	}
}

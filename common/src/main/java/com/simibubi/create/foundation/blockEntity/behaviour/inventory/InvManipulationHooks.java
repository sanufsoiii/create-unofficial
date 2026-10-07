package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class InvManipulationHooks {

	@ExpectPlatform
	public static <T> @Nullable T getIdentifiedInventory(InvManipulationBehaviour behaviour) {
		throw new AssertionError("@ExpectPlatform");
	}
}

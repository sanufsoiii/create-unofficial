package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.PlatformDispatch;

public class InvManipulationHooks {

	@SuppressWarnings("unchecked")
	public static <T> @Nullable T getIdentifiedInventory(InvManipulationBehaviour behaviour) {
		return (T) PlatformDispatch.call(
			"com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationHooksImpl",
			"com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationHooksImpl",
			"getIdentifiedInventory",
			new Class<?>[]{InvManipulationBehaviour.class},
			behaviour
		);
	}
}

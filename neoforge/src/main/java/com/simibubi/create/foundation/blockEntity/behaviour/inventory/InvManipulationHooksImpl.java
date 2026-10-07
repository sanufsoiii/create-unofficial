package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;

import net.neoforged.neoforge.items.IItemHandler;

public class InvManipulationHooksImpl {

	@SuppressWarnings("unchecked")
	public static <T> @Nullable T getIdentifiedInventory(InvManipulationBehaviour behaviour) {
		IItemHandler inventory = behaviour.getInventory();
		if (inventory == null)
			return null;

		InventoryIdentifier identifier = InventoryIdentifier.get(behaviour.getWorld(), behaviour.getTarget().getOpposite());
		return (T) new IdentifiedInventory(identifier, inventory);
	}
}

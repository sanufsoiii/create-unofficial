package com.simibubi.create.content.logistics.filter;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FilterMenuFactory {

	@ExpectPlatform
	public static AbstractContainerMenu createFilterMenu(int id, Inventory inv, Player player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static AbstractContainerMenu createAttributeFilterMenu(int id, Inventory inv, Player player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static AbstractContainerMenu createPackageFilterMenu(int id, Inventory inv, Player player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void openFilterMenu(net.minecraft.server.level.ServerPlayer player, FilterItem filterItem, ItemStack heldItem) {
		throw new AssertionError();
	}
}

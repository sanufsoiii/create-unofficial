package com.simibubi.create.content.logistics.filter;

import com.simibubi.create.foundation.utility.PlatformDispatch;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FilterMenuFactory {

	public static AbstractContainerMenu createFilterMenu(int id, Inventory inv, Player player) {
		return (AbstractContainerMenu) PlatformDispatch.call(
			"com.simibubi.create.content.logistics.filter.neoforge.FilterMenuFactoryImpl",
			"com.simibubi.create.content.logistics.filter.fabric.FilterMenuFactoryImpl",
			"createFilterMenu",
			new Class<?>[]{int.class, Inventory.class, Player.class},
			id, inv, player
		);
	}

	public static AbstractContainerMenu createAttributeFilterMenu(int id, Inventory inv, Player player) {
		return (AbstractContainerMenu) PlatformDispatch.call(
			"com.simibubi.create.content.logistics.filter.neoforge.FilterMenuFactoryImpl",
			"com.simibubi.create.content.logistics.filter.fabric.FilterMenuFactoryImpl",
			"createAttributeFilterMenu",
			new Class<?>[]{int.class, Inventory.class, Player.class},
			id, inv, player
		);
	}

	public static AbstractContainerMenu createPackageFilterMenu(int id, Inventory inv, Player player) {
		return (AbstractContainerMenu) PlatformDispatch.call(
			"com.simibubi.create.content.logistics.filter.neoforge.FilterMenuFactoryImpl",
			"com.simibubi.create.content.logistics.filter.fabric.FilterMenuFactoryImpl",
			"createPackageFilterMenu",
			new Class<?>[]{int.class, Inventory.class, Player.class},
			id, inv, player
		);
	}

	public static void openFilterMenu(net.minecraft.server.level.ServerPlayer player, FilterItem filterItem, ItemStack heldItem) {
		PlatformDispatch.call(
			"com.simibubi.create.content.logistics.filter.neoforge.FilterMenuFactoryImpl",
			"com.simibubi.create.content.logistics.filter.fabric.FilterMenuFactoryImpl",
			"openFilterMenu",
			new Class<?>[]{net.minecraft.server.level.ServerPlayer.class, FilterItem.class, ItemStack.class},
			player, filterItem, heldItem
		);
	}
}

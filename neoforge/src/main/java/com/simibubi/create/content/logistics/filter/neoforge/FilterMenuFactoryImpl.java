package com.simibubi.create.content.logistics.filter.neoforge;

import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class FilterMenuFactoryImpl {

	public static AbstractContainerMenu createFilterMenu(int id, Inventory inv, Player player) {
		return FilterMenu.create(id, inv, player.getMainHandItem());
	}

	public static AbstractContainerMenu createAttributeFilterMenu(int id, Inventory inv, Player player) {
		return AttributeFilterMenu.create(id, inv, player.getMainHandItem());
	}

	public static AbstractContainerMenu createPackageFilterMenu(int id, Inventory inv, Player player) {
		return PackageFilterMenu.create(id, inv, player.getMainHandItem());
	}

	public static void openFilterMenu(net.minecraft.server.level.ServerPlayer player, com.simibubi.create.content.logistics.filter.FilterItem filterItem, net.minecraft.world.item.ItemStack heldItem) {
		player.openMenu(filterItem, buf -> {
			net.minecraft.world.item.ItemStack.STREAM_CODEC.encode(buf, heldItem);
		});
	}
}

package com.simibubi.create.content.logistics.box;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PackageItem extends Item {
	public static final int SLOTS = 9;

	public PackageItem(Properties properties) {
		super(properties);
	}

	public static boolean isPackage(ItemStack stack) {
		return PackageItemHelper.isPackage(stack);
	}

	public static String getAddress(ItemStack box) {
		return PackageItemHelper.getAddress(box);
	}

	public static boolean matchAddress(ItemStack box, String address) {
		return PackageItemHelper.matchAddress(box, address);
	}
}

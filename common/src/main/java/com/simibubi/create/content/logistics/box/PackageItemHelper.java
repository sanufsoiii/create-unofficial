package com.simibubi.create.content.logistics.box;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.AllDataComponents;

import net.createmod.catnip.data.Glob;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

public class PackageItemHelper {

	public static boolean isPackage(ItemStack stack) {
		return stack.has(AllDataComponents.PACKAGE_ADDRESS)
			|| stack.has(AllDataComponents.PACKAGE_ORDER_DATA)
			|| stack.has(AllDataComponents.PACKAGE_ORDER_CONTEXT)
			|| stack.has(AllDataComponents.PACKAGE_CONTENTS);
	}

	public static String getAddress(ItemStack box) {
		return box.getOrDefault(AllDataComponents.PACKAGE_ADDRESS, "");
	}

	public static boolean matchAddress(ItemStack box, String address) {
		return matchAddress(getAddress(box), address);
	}

	public static boolean matchAddress(String boxAddress, String address) {
		if (address.isBlank())
			return boxAddress.isBlank();
		if (address.equals("*") || boxAddress.equals("*"))
			return true;
		if (address.equals(boxAddress))
			return true;
		return address.matches(Glob.toRegexPattern(boxAddress, "")) ||
			boxAddress.matches(Glob.toRegexPattern(address, ""));
	}

	public static boolean doPackagesHaveSameData(@NotNull ItemStack a, @NotNull ItemStack b) {
		if (a.isEmpty())
			return false;
		if (!ItemStack.isSameItemSameComponents(a, b))
			return false;
		for (TypedDataComponent<?> component : a.getComponents()) {
			DataComponentType<?> type = component.type();
			if (type.equals(AllDataComponents.PACKAGE_ORDER_DATA) ||
				type.equals(AllDataComponents.PACKAGE_ORDER_CONTEXT))
				continue;
			if (!Objects.equals(a.get(type), b.get(type)))
				return false;
		}
		return true;
	}
}

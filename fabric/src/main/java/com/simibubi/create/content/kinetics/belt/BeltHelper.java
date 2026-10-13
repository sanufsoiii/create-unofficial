package com.simibubi.create.content.kinetics.belt;

import java.util.Map;

import com.simibubi.create.AllTags.AllItemTags;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BeltHelper {

	public static Map<Item, Boolean> uprightCache = new Object2BooleanOpenHashMap<>();

	public static boolean isItemUpright(ItemStack stack) {
		return uprightCache.computeIfAbsent(stack.getItem(),
			item -> ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null
				|| AllItemTags.UPRIGHT_ON_BELT.matches(stack));
	}

}

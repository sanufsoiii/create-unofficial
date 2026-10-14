package com.simibubi.create.content.fluids.spout;

import com.simibubi.create.content.fluids.transfer.GenericItemFilling;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FillingBySpout {

	public static boolean canItemBeFilled(Level world, ItemStack stack) {
		return GenericItemFilling.canItemBeFilled(world, stack);
	}

	public static long getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
		return GenericItemFilling.getRequiredAmountForItem(world, stack, availableFluid);
	}

	public static ItemStack fillItem(Level world, long requiredAmount, ItemStack stack, FluidStack availableFluid) {
		return GenericItemFilling.fillItem(world, requiredAmount, stack, availableFluid);
	}

}

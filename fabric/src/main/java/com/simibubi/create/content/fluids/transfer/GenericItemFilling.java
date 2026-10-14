package com.simibubi.create.content.fluids.transfer;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.MutableContainerItemContext;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class GenericItemFilling {

	public static boolean isFluidHandlerValid(ItemStack stack, Storage<FluidVariant> fluidHandler) {
		return true;
	}

	public static boolean canItemBeFilled(Level world, ItemStack stack) {
		if (stack.getItem() == Items.GLASS_BOTTLE)
			return true;
		if (stack.getItem() == Items.BUCKET)
			return true;
		if (stack.getItem() == Items.MILK_BUCKET)
			return false;

		Storage<FluidVariant> tank = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
		if (tank == null)
			return false;
		if (!isFluidHandlerValid(stack, tank))
			return false;
		return tank.supportsInsertion();
	}

	public static long getRequiredAmountForItem(Level world, ItemStack stack, FluidStack availableFluid) {
		if (stack.getItem() == Items.GLASS_BOTTLE && availableFluid.getFluid().isSame(Fluids.WATER))
			return FluidConstants.BOTTLE;
		if (stack.getItem() == Items.BUCKET) {
			Fluid fluid = availableFluid.getFluid();
			if (fluid.isSame(Fluids.WATER) || fluid.isSame(Fluids.LAVA))
				return FluidConstants.BUCKET;
		}

		Storage<FluidVariant> tank = FluidStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
		if (tank == null)
			return -1;

		try (Transaction t = TransferUtil.getTransaction()) {
			long filled = tank.insert(availableFluid.getVariant(), availableFluid.getAmount(), t);
			return filled == 0 ? -1 : filled;
		}
	}

	public static ItemStack fillItem(Level world, long requiredAmount, ItemStack stack, FluidStack availableFluid) {
		Fluid fluid = availableFluid.getFluid();
		if (stack.getItem() == Items.GLASS_BOTTLE && fluid.isSame(Fluids.WATER)) {
			stack.shrink(1);
			ItemStack potion = new ItemStack(Items.POTION);
			potion.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
			return potion;
		}
		if (stack.getItem() == Items.BUCKET) {
			if (fluid.isSame(Fluids.WATER)) {
				stack.shrink(1);
				return new ItemStack(Items.WATER_BUCKET);
			}
			if (fluid.isSame(Fluids.LAVA)) {
				stack.shrink(1);
				return new ItemStack(Items.LAVA_BUCKET);
			}
		}

		ItemStack split = stack.copy();
		split.setCount(1);
		MutableContainerItemContext ctx = new MutableContainerItemContext(split);
		Storage<FluidVariant> tank = FluidStorage.ITEM.find(split, ctx);
		if (tank == null)
			return ItemStack.EMPTY;

		try (Transaction t = TransferUtil.getTransaction()) {
			tank.insert(availableFluid.getVariant(), requiredAmount, t);
			t.commit();
			stack.shrink(1);
			return ctx.getItemVariant().toStack((int) ctx.getAmount());
		}
	}
}

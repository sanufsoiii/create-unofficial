package com.simibubi.create.content.fluids.transfer;

import org.apache.commons.lang3.tuple.Pair;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.MutableContainerItemContext;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class GenericItemEmptying {

	public static boolean canItemBeEmptied(Level world, ItemStack stack) {
		if (stack.getItem() instanceof PotionItem) {
			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
			return contents != null && contents.is(Potions.WATER);
		}
		return TransferUtil.getFluidContained(stack).isPresent();
	}

	public static Pair<FluidStack, ItemStack> emptyItem(Level world, ItemStack stack, boolean simulate) {
		FluidStack resultingFluid = FluidStack.EMPTY;
		ItemStack resultingItem = ItemStack.EMPTY;

		if (stack.getItem() instanceof PotionItem) {
			PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
			if (contents != null && contents.is(Potions.WATER)) {
				if (!simulate)
					stack.shrink(1);
				return Pair.of(new FluidStack(Fluids.WATER, FluidConstants.BOTTLE), new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		ItemStack split = stack.copy();
		split.setCount(1);
		MutableContainerItemContext ctx = new MutableContainerItemContext(split);
		Storage<FluidVariant> tank = FluidStorage.ITEM.find(split, ctx);
		if (tank == null)
			return Pair.of(resultingFluid, resultingItem);
		try (Transaction t = TransferUtil.getTransaction()) {
			resultingFluid = TransferUtil.extractAnyFluid(tank, FluidConstants.BUCKET);
			resultingItem = ctx.getItemVariant().toStack((int) ctx.getAmount());
			if (!simulate) {
				stack.shrink(1);
				t.commit();
			}

			return Pair.of(resultingFluid, resultingItem);
		}
	}
}

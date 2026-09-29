package com.simibubi.create.foundation.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableInt;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Pure vanilla helper methods for ItemStacks and Ingredients.
 * Platform-independent and safe to use in common.
 */
public class CommonItemHelper {

	public static boolean sameItem(ItemStack stack, ItemStack otherStack) {
		return !otherStack.isEmpty() && stack.is(otherStack.getItem());
	}

	public static Predicate<ItemStack> sameItemPredicate(ItemStack stack) {
		return s -> sameItem(stack, s);
	}

	public static List<ItemStack> multipliedOutput(ItemStack in, ItemStack out) {
		List<ItemStack> stacks = new ArrayList<>();
		ItemStack result = out.copy();
		result.setCount(in.getCount() * out.getCount());

		while (result.getCount() > result.getMaxStackSize()) {
			stacks.add(result.split(result.getMaxStackSize()));
		}

		stacks.add(result);
		return stacks;
	}

	public static void addToList(ItemStack stack, List<ItemStack> stacks) {
		for (ItemStack s : stacks) {
			if (!ItemStack.isSameItemSameComponents(stack, s))
				continue;
			int transferred = Math.min(s.getMaxStackSize() - s.getCount(), stack.getCount());
			s.grow(transferred);
			stack.shrink(transferred);
		}
		if (stack.getCount() > 0)
			stacks.add(stack);
	}

	public static List<Pair<Ingredient, MutableInt>> condenseIngredients(NonNullList<Ingredient> recipeIngredients) {
		List<Pair<Ingredient, MutableInt>> actualIngredients = new ArrayList<>();
		Ingredients:
		for (Ingredient igd : recipeIngredients) {
			for (Pair<Ingredient, MutableInt> pair : actualIngredients) {
				ItemStack[] stacks1 = pair.getFirst()
					.getItems();
				ItemStack[] stacks2 = igd.getItems();
				if (stacks1.length != stacks2.length)
					continue;
				for (int i = 0; i <= stacks1.length; i++) {
					if (i == stacks1.length) {
						pair.getSecond()
							.increment();
						continue Ingredients;
					}
					if (!ItemStack.matches(stacks1[i], stacks2[i]))
						break;
				}
			}
			actualIngredients.add(Pair.of(igd, new MutableInt(1)));
		}
		return actualIngredients;
	}

	public static boolean matchIngredients(Ingredient i1, Ingredient i2) {
		if (i1 == i2)
			return true;
		ItemStack[] stacks1 = i1.getItems();
		ItemStack[] stacks2 = i2.getItems();
		if (stacks1 == stacks2)
			return true;
		if (stacks1.length == stacks2.length) {
			for (int i = 0; i < stacks1.length; i++)
				if (!ItemStack.isSameItem(stacks1[i], stacks2[i]))
					return false;
			return true;
		}
		return false;
	}

	public static boolean matchAllIngredients(NonNullList<Ingredient> ingredients) {
		if (ingredients.size() <= 1)
			return true;
		Ingredient firstIngredient = ingredients.get(0);
		for (int i = 1; i < ingredients.size(); i++)
			if (!matchIngredients(firstIngredient, ingredients.get(i)))
				return false;
		return true;
	}

	public static boolean canItemStackAmountsStack(ItemStack a, ItemStack b) {
		return ItemStack.isSameItemSameComponents(a, b) && a.getCount() + b.getCount() <= a.getMaxStackSize();
	}

	public static ItemStack limitCountToMaxStackSize(ItemStack stack, boolean simulate) {
		int count = stack.getCount();
		int max = stack.getMaxStackSize();
		if (count <= max)
			return ItemStack.EMPTY;
		ItemStack remainder = stack.copyWithCount(count - max);
		if (!simulate)
			stack.setCount(max);
		return remainder;
	}
}

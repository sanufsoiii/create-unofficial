package com.simibubi.create.foundation.recipe;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public class RecipeApplier {
	public static void applyRecipeOn(ItemEntity entity, Recipe<?> recipe, boolean returnProcessingRemainder) {
		List<ItemStack> stacks = applyRecipeOn(entity.level(), entity.getItem(), recipe, returnProcessingRemainder);
		if (stacks.isEmpty()) {
			entity.discard();
			return;
		}
		entity.setItem(stacks.removeFirst());
		for (ItemStack additional : stacks) {
			ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
			entityIn.setDeltaMovement(entity.getDeltaMovement());
			entity.level().addFreshEntity(entityIn);
		}
	}

	public static List<ItemStack> applyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe) {
		return applyRecipeOn(level, stackIn, recipe, false);
	}

	public static List<ItemStack> applyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder) {
		ItemStack out = recipe.getResultItem(level.registryAccess()).copy();
		return ItemHelper.multipliedOutput(stackIn, out);
	}
}

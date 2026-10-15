package com.simibubi.create.content.processing.basin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankHooksImpl;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SizedFluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.createmod.catnip.data.Iterate;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

public class BasinRecipe extends StandardProcessingRecipe<RecipeInput> {

	public static boolean match(BasinBlockEntity basin, Recipe<?> recipe) {
		FilteringBehaviour filter = basin.getFilter();
		if (filter == null)
			return false;

		boolean filterTest = filter.test(recipe.getResultItem(basin.getLevel()
			.registryAccess()));
		if (recipe instanceof BasinRecipe basinRecipe) {
			if (basinRecipe.getRollableResults()
				.isEmpty()
				&& !basinRecipe.getFluidResults()
				.isEmpty())
				filterTest = FluidHelper.test(filter, basinRecipe.getFluidResults()
					.get(0));
		}

		if (!filterTest)
			return false;

		return apply(basin, recipe, true);
	}

	public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe) {
		return apply(basin, recipe, false);
	}

	private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
		boolean isBasinRecipe = recipe instanceof BasinRecipe;
		SmartInventory availableItems = basin.getInputInventory();
		SmartFluidTankBehaviour inputTank = basin.getBehaviour(SmartFluidTankBehaviour.INPUT);

		if (availableItems == null)
			return false;

		HeatLevel heat = basin.getHeatLevel();
		if (isBasinRecipe && !((BasinRecipe) recipe).getRequiredHeat()
			.testBlazeBurner(heat))
			return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();

		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
		List<SizedFluidIngredient> fluidIngredients =
			isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

		TankSegment[] availableTanks = inputTank != null ? inputTank.getTanks() : new TankSegment[0];

		for (boolean simulate : Iterate.trueAndFalse) {

			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlotCount()];
			int[] extractedFluidsFromTank = new int[availableTanks.length];

			Ingredients:
			for (Ingredient ingredient : ingredients) {
				for (int slot = 0; slot < availableItems.getSlotCount(); slot++) {
					ItemStack stack = availableItems.getStackInSlot(slot);
					if (simulate && stack.getCount() <= extractedItemsFromSlot[slot])
						continue;
					if (!ingredient.test(stack))
						continue;
					if (!simulate) {
						stack.shrink(1);
						availableItems.setStackInSlot(slot, stack);
						availableItems.setChanged();
					}
					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				return false;
			}

			boolean fluidsAffected = false;
			FluidIngredients:
			for (SizedFluidIngredient fluidIngredient : fluidIngredients) {
				int amountRequired = fluidIngredient.amount();

				for (int tank = 0; tank < availableTanks.length; tank++) {
					TankSegment segment = availableTanks[tank];
					FluidStack fluidStack = getFluidInTank(segment);
					if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
						continue;
					if (!fluidIngredient.test(fluidStack))
						continue;
					int drainedAmount = (int) Math.min((long) amountRequired, fluidStack.getAmount() - extractedFluidsFromTank[tank]);
					if (drainedAmount <= 0)
						continue;
					if (!simulate) {
						drainTank(segment, drainedAmount);
						fluidsAffected = true;
					}
					amountRequired -= drainedAmount;
					extractedFluidsFromTank[tank] += drainedAmount;
					if (amountRequired <= 0)
						continue FluidIngredients;
				}

				return false;
			}

			if (fluidsAffected) {
				basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
					.forEach(TankSegment::onFluidStackChanged);
				basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
					.forEach(TankSegment::onFluidStackChanged);
			}

			if (simulate) {
				CraftingInput remainderInput = new DummyCraftingContainer(availableItems, extractedItemsFromSlot)
					.asCraftInput();

				if (recipe instanceof BasinRecipe basinRecipe) {
					recipeOutputItems.addAll(basinRecipe.rollResults(basin.getLevel().random));

					for (FluidStack fluidStack : basinRecipe.getFluidResults())
						if (!fluidStack.isEmpty())
							recipeOutputFluids.add(fluidStack);
					for (ItemStack stack : basinRecipe.getRemainingItems(remainderInput))
						if (!stack.isEmpty())
							recipeOutputItems.add(stack);

				} else {
					recipeOutputItems.add(recipe.getResultItem(basin.getLevel()
						.registryAccess()));

					if (recipe instanceof CraftingRecipe craftingRecipe) {
						for (ItemStack stack : craftingRecipe.getRemainingItems(remainderInput))
							if (!stack.isEmpty())
								recipeOutputItems.add(stack);
					}
				}
			}

			if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;
		}

		return true;
	}

	private static FluidStack getFluidInTank(TankSegment segment) {
		if (segment instanceof SmartFluidTankHooksImpl.FabricTankSegment fabricSegment) {
			if (fabricSegment.isEmpty())
				return FluidStack.EMPTY;
			try {
				var storageField = fabricSegment.getClass().getDeclaredField("storage");
				storageField.setAccessible(true);
				var storage = (net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant>) storageField.get(fabricSegment);
				if (storage.variant.isBlank() || storage.amount <= 0)
					return FluidStack.EMPTY;
				return new FluidStack(storage.variant, storage.amount);
			} catch (Exception ignored) {
			}
		}
		return FluidStack.EMPTY;
	}

	private static void drainTank(TankSegment segment, long amount) {
		if (segment instanceof SmartFluidTankHooksImpl.FabricTankSegment fabricSegment) {
			try {
				var storageField = fabricSegment.getClass().getDeclaredField("storage");
				storageField.setAccessible(true);
				var storage = (net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant>) storageField.get(fabricSegment);
				try (Transaction t = Transaction.openOuter()) {
					storage.extract(storage.variant, amount, t);
					t.commit();
				}
			} catch (Exception ignored) {
			}
		}
	}

	public static RecipeHolder<BasinRecipe> convertShapeless(RecipeHolder<?> recipe) {
		BasinRecipe basinRecipe =
			new Builder<>(BasinRecipe::new, recipe.id()).withItemIngredients(recipe.value().getIngredients())
				.withSingleItemOutput(recipe.value().getResultItem(Minecraft.getInstance().level.registryAccess()))
				.build();
		return new RecipeHolder<>(recipe.id(), basinRecipe);
	}

	protected BasinRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
		super(type, params);
	}

	public BasinRecipe(ProcessingRecipeParams params) {
		this(AllRecipeTypes.BASIN, params);
	}

	@Override
	protected int getMaxInputCount() {
		return 64;
	}

	@Override
	protected int getMaxOutputCount() {
		return 4;
	}

	@Override
	protected int getMaxFluidInputCount() {
		return 2;
	}

	@Override
	protected int getMaxFluidOutputCount() {
		return 2;
	}

	@Override
	protected boolean canRequireHeat() {
		return true;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Override
	public boolean matches(RecipeInput input, @NotNull Level worldIn) {
		return false;
	}
}

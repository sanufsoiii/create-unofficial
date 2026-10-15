package com.simibubi.create.content.kinetics.deployer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.recipe.RecipeWrapper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ItemApplicationRecipe extends ProcessingRecipe<RecipeWrapper, ItemApplicationRecipeParams> {

	private boolean keepHeldItem;

	public ItemApplicationRecipe(IRecipeTypeInfo type, ItemApplicationRecipeParams params) {
		super(type, params);
		keepHeldItem = params.keepHeldItem;
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level p_77569_2_) {
		return getProcessedItem().test(inv.getItem(0)) && getRequiredHeldItem().test(inv.getItem(1));
	}

	@Override
	protected int getMaxInputCount() {
		return 2;
	}

	@Override
	protected int getMaxOutputCount() {
		return 4;
	}

	public boolean shouldKeepHeldItem() {
		return keepHeldItem;
	}

	public Ingredient getRequiredHeldItem() {
		if (ingredients.size() < 2)
			throw new IllegalStateException("Item Application Recipe has no tool!");
		return ingredients.get(1);
	}

	public Ingredient getProcessedItem() {
		if (ingredients.isEmpty())
			throw new IllegalStateException("Item Application Recipe has no ingredient!");
		return ingredients.get(0);
	}

	@FunctionalInterface
	public interface Factory<R extends ItemApplicationRecipe> extends ProcessingRecipe.Factory<ItemApplicationRecipeParams, R> {
		R create(ItemApplicationRecipeParams params);
	}

	public static class Builder<R extends ItemApplicationRecipe> {
		protected Factory<R> factory;
		protected ResourceLocation recipeId;
		protected ItemApplicationRecipeParams params;

		public Builder(Factory<R> factory, ResourceLocation recipeId) {
			this.factory = factory;
			this.recipeId = recipeId;
			this.params = new ItemApplicationRecipeParams();
		}

		public Builder<R> require(Ingredient ingredient) {
			params.ingredients.add(ingredient);
			return this;
		}

		public Builder<R> require(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag) {
			return require(Ingredient.of(tag));
		}

		public Builder<R> output(com.simibubi.create.content.processing.recipe.ProcessingOutput output) {
			params.results.add(output);
			return this;
		}

		public Builder<R> output(ItemStack output) {
			return output(new com.simibubi.create.content.processing.recipe.ProcessingOutput(output, 1.0f));
		}

		public Builder<R> toolNotConsumed() {
			params.keepHeldItem = true;
			return this;
		}

		public R build() {
			return factory.create(params);
		}
	}

	public static class Serializer<R extends ItemApplicationRecipe> implements RecipeSerializer<R> {
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(ProcessingRecipe.Factory<ItemApplicationRecipeParams, R> factory) {
			this.codec = ProcessingRecipe.codec(factory, ItemApplicationRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, ItemApplicationRecipeParams.STREAM_CODEC);
		}

		@Override
		public MapCodec<R> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		}
	}
}

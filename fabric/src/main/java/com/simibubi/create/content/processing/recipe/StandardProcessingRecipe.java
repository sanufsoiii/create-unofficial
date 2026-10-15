package com.simibubi.create.content.processing.recipe;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class StandardProcessingRecipe<T extends RecipeInput> extends ProcessingRecipe<T, ProcessingRecipeParams> {
	public StandardProcessingRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
		super(typeInfo, params);
	}

	@FunctionalInterface
	public interface Factory<R extends StandardProcessingRecipe<?>> extends ProcessingRecipe.Factory<ProcessingRecipeParams, R> {
		R create(ProcessingRecipeParams params);
	}

	public static class Builder<R extends StandardProcessingRecipe<?>> {
		protected Factory<R> factory;
		protected ResourceLocation recipeId;
		protected ProcessingRecipeParams params;

		public Builder(Factory<R> factory, ResourceLocation recipeId) {
			this.factory = factory;
			this.recipeId = recipeId;
			this.params = new ProcessingRecipeParams();
		}

		public Builder<R> withItemIngredients(net.minecraft.core.NonNullList<net.minecraft.world.item.crafting.Ingredient> ingredients) {
			params.ingredients = ingredients;
			return this;
		}

		public Builder<R> withSingleItemOutput(net.minecraft.world.item.ItemStack output) {
			params.results.add(new ProcessingOutput(output, 1.0f));
			return this;
		}

		public R build() {
			return factory.create(params);
		}
	}

	public static class Serializer<R extends StandardProcessingRecipe<?>> implements RecipeSerializer<R> {
		private final Factory<R> factory;
		private final MapCodec<R> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

		public Serializer(Factory<R> factory) {
			this.factory = factory;
			this.codec = ProcessingRecipe.codec(factory, ProcessingRecipeParams.CODEC);
			this.streamCodec = ProcessingRecipe.streamCodec(factory, ProcessingRecipeParams.STREAM_CODEC);
		}

		@Override
		public MapCodec<R> codec() {
			return codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
			return streamCodec;
		}

		public Factory<R> factory() {
			return factory;
		}
	}
}

package com.simibubi.create;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

public enum AllRecipeTypes implements IRecipeTypeInfo, StringRepresentable {

	MECHANICAL_CRAFTING(MechanicalCraftingRecipe.Serializer::new);

	public static final Predicate<RecipeHolder<?>> CAN_BE_AUTOMATED = r -> !r.id()
			.getPath()
			.endsWith("_manual_only");

	public final ResourceLocation id;
	public final Supplier<RecipeSerializer<?>> serializerSupplier;
	private RecipeSerializer<?> serializerObject;
	private RecipeType<?> typeObject;

	AllRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
		String name = name().toLowerCase(java.util.Locale.ROOT);
		id = Create.asResource(name);
		this.serializerSupplier = serializerSupplier;
	}

	public static void register() {
		// ShapedRecipePattern.setCraftingSize(9, 9);
		for (AllRecipeTypes entry : values()) {
			entry.serializerObject = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, entry.id, entry.serializerSupplier.get());
			entry.typeObject = Registry.register(BuiltInRegistries.RECIPE_TYPE, entry.id, new RecipeType<Recipe<RecipeInput>>() {
				@Override
				public String toString() {
					return entry.id.toString();
				}
			});
		}
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RecipeSerializer<?>> T getSerializer() {
		return (T) serializerObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
		return (RecipeType<R>) typeObject;
	}

	public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I inv, Level world) {
		return world.getRecipeManager()
			.getRecipeFor(getType(), inv, world);
	}

	public static boolean shouldIgnoreInAutomation(RecipeHolder<?> recipe) {
		RecipeSerializer<?> serializer = recipe.value().getSerializer();
		if (serializer != null && AllTags.AllRecipeSerializerTags.AUTOMATION_IGNORE.matches(serializer))
			return true;
		return !CAN_BE_AUTOMATED.test(recipe);
	}

	@Override
	public @NotNull String getSerializedName() {
		return id.toString();
	}
}

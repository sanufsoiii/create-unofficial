package com.simibubi.create.content.trains.track;

import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class TrackMaterialFactory {
	private final ResourceLocation id;
	private String langName;
	private NonNullSupplier<NonNullSupplier<? extends TrackBlock>> trackBlock;
	private Ingredient sleeperIngredient = Ingredient.EMPTY;
	private Ingredient railsIngredient = Ingredient.of(CommonMetal.IRON.nuggets);
	private ResourceLocation particle;
	private TrackMaterial.TrackType trackType = TrackMaterial.TrackType.STANDARD;

	@Nullable
	private TrackMaterial.TrackType.TrackBlockFactory customFactory = null;

	public TrackMaterialFactory(ResourceLocation id) {
		this.id = id;
	}

	public static TrackMaterialFactory make(ResourceLocation id) {
		return new TrackMaterialFactory(id);
	}

	public TrackMaterialFactory lang(String langName) {
		this.langName = langName;
		return this;
	}

	public TrackMaterialFactory block(NonNullSupplier<NonNullSupplier<? extends TrackBlock>> trackBlock) {
		this.trackBlock = trackBlock;
		return this;
	}

	public TrackMaterialFactory defaultModels() {
		return this;
	}

	public TrackMaterialFactory sleeper(Ingredient sleeperIngredient) {
		this.sleeperIngredient = sleeperIngredient;
		return this;
	}

	public TrackMaterialFactory rails(Ingredient railsIngredient) {
		this.railsIngredient = railsIngredient;
		return this;
	}

	public TrackMaterialFactory particle(ResourceLocation particle) {
		this.particle = particle;
		return this;
	}

	public TrackMaterialFactory trackType(TrackMaterial.TrackType trackType) {
		this.trackType = trackType;
		return this;
	}

	public TrackMaterialFactory customFactory(TrackMaterial.TrackType.TrackBlockFactory customFactory) {
		this.customFactory = customFactory;
		return this;
	}

	public TrackMaterial build() {
		return new TrackMaterial(id, langName, trackBlock, particle, sleeperIngredient, railsIngredient, trackType, customFactory);
	}
}

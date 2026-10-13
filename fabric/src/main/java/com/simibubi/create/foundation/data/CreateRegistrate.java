package com.simibubi.create.foundation.data;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CreateRegistrate extends AbstractRegistrate<CreateRegistrate> {
	private static final Map<RegistryEntry<?, ?>, ResourceKey<CreativeModeTab>> TAB_LOOKUP = Collections.synchronizedMap(new IdentityHashMap<>());

	@Nullable
	protected Function<Item, TooltipModifier> currentTooltipModifierFactory;
	@Nullable
	protected ResourceKey<CreativeModeTab> currentTab;

	protected CreateRegistrate(String modid) {
		super(modid);
	}

	public static CreateRegistrate create(String modid) {
		return new CreateRegistrate(modid);
	}

	public static boolean isInCreativeTab(RegistryEntry<?, ?> entry, ResourceKey<CreativeModeTab> tab) {
		return TAB_LOOKUP.get(entry) == tab;
	}

	public CreateRegistrate setTooltipModifierFactory(@Nullable Function<Item, TooltipModifier> factory) {
		currentTooltipModifierFactory = factory;
		return self();
	}

	@Nullable
	public Function<Item, TooltipModifier> getTooltipModifierFactory() {
		return currentTooltipModifierFactory;
	}

	public CreateRegistrate setCreativeTab(ResourceKey<CreativeModeTab> tab) {
		this.currentTab = tab;
		return self();
	}

	public CreateRegistrate setCreativeTab(com.simibubi.create.AllCreativeModeTabs.TabInfo tab) {
		this.currentTab = tab.key();
		return self();
	}

	@Override
	protected <R, T extends R> RegistryEntry<R, T> accept(String name, ResourceKey<? extends Registry<R>> type,
			Builder<R, T, ?, ?> builder, NonNullSupplier<? extends T> creator,
			NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
		RegistryEntry<R, T> entry = super.accept(name, type, builder, creator, entryFactory);
		if (type.equals(Registries.ITEM)) {
			if (currentTooltipModifierFactory != null) {
				Function<Item, TooltipModifier> factory = currentTooltipModifierFactory;
				builder.onRegister(obj -> {
					if (obj instanceof Item item) {
						TooltipModifier modifier = factory.apply(item);
						if (modifier != null) {
							TooltipModifier.REGISTRY.register(item, modifier);
						}
					}
				});
			}
		}
		if (currentTab != null) {
			TAB_LOOKUP.put(entry, currentTab);
		}
		return entry;
	}

	public static <T extends Block> com.tterrag.registrate.util.nullness.NonNullConsumer<? super T> casingConnectivity(
			java.util.function.BiConsumer<T, com.simibubi.create.content.decoration.encasing.CasingConnectivity> consumer) {
		return entry -> {};
	}

	public static <T extends Block> com.tterrag.registrate.util.nullness.NonNullConsumer<? super T> blockModel(
			java.util.function.Supplier<NonNullFunction<net.minecraft.client.resources.model.BakedModel, ? extends net.minecraft.client.resources.model.BakedModel>> func) {
		return entry -> {};
	}

	public static <T extends Item> com.tterrag.registrate.util.nullness.NonNullConsumer<? super T> itemModel(
			java.util.function.Supplier<NonNullFunction<net.minecraft.client.resources.model.BakedModel, ? extends net.minecraft.client.resources.model.BakedModel>> func) {
		return entry -> {};
	}

	public static com.tterrag.registrate.util.nullness.NonNullConsumer<? super Block> connectedTextures(
			java.util.function.Supplier<com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour> behavior) {
		return entry -> {};
	}
}

package com.simibubi.create;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AllCreativeModeTabs {

	public record TabInfo(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
	}

	public static final TabInfo BASE_CREATIVE_TAB = register("base",
			() -> FabricItemGroup.builder()
					.title(Component.translatable("itemGroup.create.base"))
					.icon(() -> new ItemStack(Items.COPPER_INGOT))
					.displayItems((params, output) -> {})
					.build());

	public static final TabInfo PALETTES_CREATIVE_TAB = register("palettes",
			() -> FabricItemGroup.builder()
					.title(Component.translatable("itemGroup.create.palettes"))
					.icon(() -> new ItemStack(Items.SMOOTH_STONE))
					.displayItems((params, output) -> {})
					.build());

	private static TabInfo register(String name, Supplier<CreativeModeTab> supplier) {
		ResourceLocation id = Create.asResource(name);
		ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
		CreativeModeTab tab = supplier.get();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
		return new TabInfo(key, tab);
	}

	public static void register() {
		// Class loading registers tabs
	}
}

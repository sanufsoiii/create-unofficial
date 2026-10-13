package com.simibubi.create;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;

import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class AllCreativeModeTabs {

	public record TabInfo(ResourceKey<CreativeModeTab> key, CreativeModeTab tab) {
	}

	public static final TabInfo BASE_CREATIVE_TAB = register("base",
			() -> FabricItemGroup.builder()
					.title(Component.translatable("itemGroup.create.base"))
					.icon(() -> new ItemStack(AllItems.WRENCH.get()))
					.displayItems(new RegistrateDisplayItemsGenerator(true, () -> AllCreativeModeTabs.BASE_CREATIVE_TAB))
					.build());

	public static final TabInfo PALETTES_CREATIVE_TAB = register("palettes",
			() -> FabricItemGroup.builder()
					.title(Component.translatable("itemGroup.create.palettes"))
					.icon(() -> new ItemStack(AllBlocks.ANDESITE_CASING.get()))
					.displayItems(new RegistrateDisplayItemsGenerator(false, () -> AllCreativeModeTabs.PALETTES_CREATIVE_TAB))
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

	private static class RegistrateDisplayItemsGenerator implements DisplayItemsGenerator {
		private final boolean addItems;
		private final Supplier<TabInfo> tabFilter;

		public RegistrateDisplayItemsGenerator(boolean addItems, Supplier<TabInfo> tabFilter) {
			this.addItems = addItems;
			this.tabFilter = tabFilter;
		}

		@Override
		public void accept(ItemDisplayParameters parameters, Output output) {
			ResourceKey<CreativeModeTab> tabKey = tabFilter.get().key();
			List<Item> items = new ArrayList<>();

			if (addItems) {
				for (RegistryEntry<Item, ?> entry : CreateFabric.REGISTRATE.getAll(Registries.ITEM)) {
					if (!CreateRegistrate.isInCreativeTab(entry, tabKey))
						continue;
					Item item = entry.get();
					if (item instanceof BlockItem)
						continue;
					items.add(item);
				}
			}

			for (RegistryEntry<Block, ?> entry : CreateFabric.REGISTRATE.getAll(Registries.BLOCK)) {
				if (!CreateRegistrate.isInCreativeTab(entry, tabKey))
					continue;
				Item item = entry.get().asItem();
				if (item == Items.AIR)
					continue;
				items.add(item);
			}

			for (Item item : new LinkedHashSet<>(items)) {
				output.accept(new ItemStack(item));
			}
		}
	}
}

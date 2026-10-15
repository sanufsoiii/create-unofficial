package com.simibubi.create;

import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxScreen;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.content.logistics.filter.PackageFilterScreen;
import com.simibubi.create.content.logistics.packagePort.PackagePortMenu;
import com.simibubi.create.content.logistics.packagePort.PackagePortScreen;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import com.simibubi.create.content.schematics.cannon.SchematicannonScreen;
import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import com.simibubi.create.content.schematics.table.SchematicTableScreen;
import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateMenuBuilder.MenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AllMenuTypes {

	public static final MenuEntry<SchematicTableMenu> SCHEMATIC_TABLE =
		register("schematic_table", SchematicTableMenu::new, () -> SchematicTableScreen::new);

	public static final MenuEntry<SchematicannonMenu> SCHEMATICANNON =
		register("schematicannon", SchematicannonMenu::new, () -> SchematicannonScreen::new);

	public static final MenuEntry<FilterMenu> FILTER =
		register("filter", FilterMenu::new, () -> FilterScreen::new);

	public static final MenuEntry<AttributeFilterMenu> ATTRIBUTE_FILTER =
		register("attribute_filter", AttributeFilterMenu::new, () -> AttributeFilterScreen::new);

	public static final MenuEntry<PackageFilterMenu> PACKAGE_FILTER =
		register("package_filter", PackageFilterMenu::new, () -> PackageFilterScreen::new);

	public static final MenuEntry<ToolboxMenu> TOOLBOX =
		register("toolbox", ToolboxMenu::new, () -> ToolboxScreen::new);

	public static final MenuEntry<PackagePortMenu> PACKAGE_PORT =
		register("package_port", PackagePortMenu::new, () -> PackagePortScreen::new);

	public static final MenuEntry<RedstoneRequesterMenu> REDSTONE_REQUESTER =
		register("redstone_requester", RedstoneRequesterMenu::new, () -> RedstoneRequesterScreen::new);

	public static final MenuEntry<com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemMenu> FACTORY_PANEL_SET_ITEM =
		register("factory_panel_set_item", com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemMenu::new, () -> com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemScreen::new);

	private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
		String name, MenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
		return CreateFabric.REGISTRATE
			.menu(name, factory, screenFactory)
			.register();
	}

	public static void register() {
	}
}

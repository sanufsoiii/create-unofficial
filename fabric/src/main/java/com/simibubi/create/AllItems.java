package com.simibubi.create;

import static com.simibubi.create.AllTags.AllItemTags.CREATE_INGOTS;
import static com.simibubi.create.AllTags.AllItemTags.CRUSHED_RAW_MATERIALS;
import static com.simibubi.create.AllTags.AllItemTags.PLATES;
import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.ALUMINUM;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.LEAD;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.NICKEL;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.OSMIUM;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.PLATINUM;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.QUICKSILVER;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.SILVER;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.TIN;
import static com.simibubi.create.foundation.data.recipe.CommonMetal.URANIUM;

import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.equipment.BuildersTeaItem;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.logistics.filter.AttributeFilterItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.PackageFilterItem;
import com.simibubi.create.content.materials.ExperienceNuggetItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class AllItems {
	private static final CreateRegistrate REGISTRATE = CreateFabric.REGISTRATE;

	static {
		REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
	}

	public static final ItemEntry<Item>
		WHEAT_FLOUR = taggedIngredient("wheat_flour", AllItemTags.FLOURS.tag, AllItemTags.WHEAT_FLOURS.tag),
		DOUGH = taggedIngredient("dough", forgeItemTag("dough"), AllItemTags.FOODS_DOUGH_WHEAT.tag),
		CINDER_FLOUR = ingredient("cinder_flour"),
		ROSE_QUARTZ = ingredient("rose_quartz"),
		POLISHED_ROSE_QUARTZ = ingredient("polished_rose_quartz"),
		POWDERED_OBSIDIAN = ingredient("powdered_obsidian"),
		STURDY_SHEET = taggedIngredient("sturdy_sheet", AllItemTags.OBSIDIAN_PLATES.tag, PLATES.tag),
		PROPELLER = ingredient("propeller"),
		WHISK = ingredient("whisk"),
		BRASS_HAND = ingredient("brass_hand"),
		CRAFTER_SLOT_COVER = ingredient("crafter_slot_cover"),
		ELECTRON_TUBE = ingredient("electron_tube");

	public static final ItemEntry<Item> CARDBOARD = REGISTRATE.item("cardboard", Item::new)
		.tag(AllItemTags.CARDBOARD_PLATES.tag, PLATES.tag)
		.register();

	public static final ItemEntry<SequencedAssemblyItem>
		INCOMPLETE_PRECISION_MECHANISM = sequencedIngredient("incomplete_precision_mechanism"),
		INCOMPLETE_REINFORCED_SHEET = sequencedIngredient("unprocessed_obsidian_sheet"),
		INCOMPLETE_TRACK = sequencedIngredient("incomplete_track");

	public static final ItemEntry<Item> PRECISION_MECHANISM = ingredient("precision_mechanism");

	public static final ItemEntry<com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem> VERTICAL_GEARBOX =
		REGISTRATE.item("vertical_gearbox", com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem::new)
			.register();

	public static final ItemEntry<Item> BLAZE_CAKE_BASE = REGISTRATE.item("blaze_cake_base", Item::new)
		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.register();

	public static final ItemEntry<Item> BLAZE_CAKE = REGISTRATE.item("blaze_cake", Item::new)
		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.register();

	public static final ItemEntry<Item> CREATIVE_BLAZE_CAKE = REGISTRATE.item("creative_blaze_cake", Item::new)
		.properties(p -> p.rarity(Rarity.EPIC))
		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.register();

	public static final ItemEntry<Item> BAR_OF_CHOCOLATE = REGISTRATE.item("bar_of_chocolate", Item::new)
		.properties(p -> p.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.3F).build()))
		.register();

	public static final ItemEntry<Item> SWEET_ROLL = REGISTRATE.item("sweet_roll", Item::new)
		.properties(p -> p.food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.8F).build()))
		.register();

	public static final ItemEntry<Item> CHOCOLATE_BERRIES = REGISTRATE.item("chocolate_glazed_berries", Item::new)
		.properties(p -> p.food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.8F).build()))
		.register();

	public static final ItemEntry<Item> HONEYED_APPLE = REGISTRATE.item("honeyed_apple", Item::new)
		.properties(p -> p.food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()))
		.register();

	public static final ItemEntry<BuildersTeaItem> BUILDERS_TEA = REGISTRATE.item("builders_tea", BuildersTeaItem::new)
		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.properties(p -> p.stacksTo(16))
		.register();

	public static final ItemEntry<Item> RAW_ZINC =
		taggedIngredient("raw_zinc", forgeItemTag("raw_materials/zinc"), forgeItemTag("raw_ores"));

	public static final ItemEntry<Item> ANDESITE_ALLOY = taggedIngredient("andesite_alloy", CREATE_INGOTS.tag),
		ZINC_INGOT = taggedIngredient("zinc_ingot", forgeItemTag("ingots/zinc"), CREATE_INGOTS.tag),
		BRASS_INGOT = taggedIngredient("brass_ingot", forgeItemTag("ingots/brass"), CREATE_INGOTS.tag);

	public static final ItemEntry<Item> COPPER_NUGGET =
		taggedIngredient("copper_nugget", forgeItemTag("nuggets/copper"), forgeItemTag("nuggets")),
		ZINC_NUGGET = taggedIngredient("zinc_nugget", forgeItemTag("nuggets/zinc"), forgeItemTag("nuggets")),
		BRASS_NUGGET = taggedIngredient("brass_nugget", forgeItemTag("nuggets/brass"), forgeItemTag("nuggets"));

	public static final ItemEntry<ExperienceNuggetItem> EXP_NUGGET =
		REGISTRATE.item("experience_nugget", ExperienceNuggetItem::new)
			.tag(forgeItemTag("nuggets"))
			.properties(p -> p.rarity(Rarity.UNCOMMON))
			.register();

	public static final ItemEntry<Item> COPPER_SHEET =
		taggedIngredient("copper_sheet", forgeItemTag("plates/copper"), PLATES.tag),
		BRASS_SHEET = taggedIngredient("brass_sheet", forgeItemTag("plates/brass"), PLATES.tag),
		IRON_SHEET = taggedIngredient("iron_sheet", forgeItemTag("plates/iron"), PLATES.tag),
		GOLDEN_SHEET = taggedIngredient("golden_sheet", forgeItemTag("plates/gold"), PLATES.tag, ItemTags.PIGLIN_LOVED),

		CRUSHED_IRON = taggedIngredient("crushed_raw_iron", CRUSHED_RAW_MATERIALS.tag),
		CRUSHED_GOLD = taggedIngredient("crushed_raw_gold", CRUSHED_RAW_MATERIALS.tag, ItemTags.PIGLIN_LOVED),
		CRUSHED_COPPER = taggedIngredient("crushed_raw_copper", CRUSHED_RAW_MATERIALS.tag),
		CRUSHED_ZINC = taggedIngredient("crushed_raw_zinc", CRUSHED_RAW_MATERIALS.tag);

	public static final ItemEntry<TagDependentIngredientItem> CRUSHED_OSMIUM = compatCrushedOre(OSMIUM),
		CRUSHED_PLATINUM = compatCrushedOre(PLATINUM),
		CRUSHED_SILVER = compatCrushedOre(SILVER),
		CRUSHED_TIN = compatCrushedOre(TIN),
		CRUSHED_LEAD = compatCrushedOre(LEAD),
		CRUSHED_QUICKSILVER = compatCrushedOre(QUICKSILVER),
		CRUSHED_BAUXITE = compatCrushedOre(ALUMINUM),
		CRUSHED_URANIUM = compatCrushedOre(URANIUM),
		CRUSHED_NICKEL = compatCrushedOre(NICKEL);

	public static final ItemEntry<WrenchItem> WRENCH = REGISTRATE.item("wrench", WrenchItem::new)
		.properties(p -> p.stacksTo(1))
		.tag(forgeItemTag("tools/wrenches"))
		.register();

	public static final ItemEntry<GogglesItem> GOGGLES = REGISTRATE.item("goggles", GogglesItem::new)
		.properties(p -> p.stacksTo(1))
		.register();

	public static final ItemEntry<com.simibubi.create.content.logistics.filter.ListFilterItem> FILTER = REGISTRATE.item("filter", FilterItem::regular)
		.register();

	public static final ItemEntry<AttributeFilterItem> ATTRIBUTE_FILTER =
		REGISTRATE.item("attribute_filter", FilterItem::attribute)
			.register();

	public static final ItemEntry<PackageFilterItem> PACKAGE_FILTER =
		REGISTRATE.item("package_filter", FilterItem::address)
			.register();

	public static final ItemEntry<TreeFertilizerItem> TREE_FERTILIZER =
		REGISTRATE.item("tree_fertilizer", TreeFertilizerItem::new)
			.register();

	public static final ItemEntry<Item> POTATO_CANNON =
		REGISTRATE.item("potato_cannon", Item::new)
			.properties(p -> p.durability(100))
			.register();

	// Shortcuts
	private static ItemEntry<Item> ingredient(String name) {
		return REGISTRATE.item(name, Item::new)
			.register();
	}

	private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
		return REGISTRATE.item(name, SequencedAssemblyItem::new)
			.register();
	}

	@SafeVarargs
	private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
		return REGISTRATE.item(name, Item::new)
			.tag(tags)
			.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatCrushedOre(CommonMetal metal) {
		return REGISTRATE
			.item("crushed_raw_" + metal,
				props -> new TagDependentIngredientItem(props, metal.ores.items()))
			.tag(CRUSHED_RAW_MATERIALS.tag)
			.register();
	}

	public static void register() {
	}
}

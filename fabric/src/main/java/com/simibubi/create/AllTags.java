package com.simibubi.create;

import static com.simibubi.create.AllTags.NameSpace.COMMON;
import static com.simibubi.create.AllTags.NameSpace.MOD;
import static com.simibubi.create.AllTags.NameSpace.QUARK;
import static com.simibubi.create.AllTags.NameSpace.TIC;

import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class AllTags {

	public static <T> TagKey<T> optionalTag(Registry<T> registry, ResourceLocation id) {
		return TagKey.create(registry.key(), id);
	}

	public static <T> TagKey<T> commonTag(Registry<T> registry, String path) {
		return optionalTag(registry, ResourceLocation.fromNamespaceAndPath("c", path));
	}

	public static TagKey<Block> commonBlockTag(String path) {
		return commonTag(BuiltInRegistries.BLOCK, path);
	}

	public static TagKey<Item> commonItemTag(String path) {
		return commonTag(BuiltInRegistries.ITEM, path);
	}

	public static TagKey<Fluid> commonFluidTag(String path) {
		return commonTag(BuiltInRegistries.FLUID, path);
	}

	public static TagKey<Block> forgeBlockTag(String path) {
		return commonBlockTag(path);
	}

	public static TagKey<Item> forgeItemTag(String path) {
		return commonItemTag(path);
	}

	public static TagKey<Fluid> forgeFluidTag(String path) {
		return commonFluidTag(path);
	}

	public enum NameSpace {
		MOD(Create.ID, false),
		COMMON("c"),
		TIC("tconstruct"),
		QUARK("quark");

		public final String id;
		public final boolean optionalDefault;

		NameSpace(String id) {
			this(id, true);
		}

		NameSpace(String id, boolean optionalDefault) {
			this.id = id;
			this.optionalDefault = optionalDefault;
		}

		public ResourceLocation id(Enum<?> entry, @Nullable String pathOverride) {
			return ResourceLocation.fromNamespaceAndPath(id, pathOverride != null ? pathOverride : Lang.asId(entry.name()));
		}
	}

	public enum AllBlockTags {
		BRITTLE,
		CASING,
		CONTRAPTION_INVENTORY_DENY,
		COPYCAT_ALLOW,
		COPYCAT_DENY,
		FAN_PROCESSING_CATALYSTS_BLASTING(MOD, "fan_processing_catalysts/blasting"),
		FAN_PROCESSING_CATALYSTS_HAUNTING(MOD, "fan_processing_catalysts/haunting"),
		FAN_PROCESSING_CATALYSTS_SMOKING(MOD, "fan_processing_catalysts/smoking"),
		FAN_PROCESSING_CATALYSTS_SPLASHING(MOD, "fan_processing_catalysts/splashing"),
		FAN_TRANSPARENT,
		GIRDABLE_TRACKS,
		MOVABLE_EMPTY_COLLIDER,
		NON_MOVABLE,
		ORE_OVERRIDE_STONE,
		PASSIVE_BOILER_HEATERS,
		SAFE_NBT,
		SEATS,
		TOOLBOXES,
		TRACKS,
		TREE_ATTACHMENTS,
		VALVE_HANDLES,
		WINDMILL_SAILS,
		WRENCH_PICKUP,

		RELOCATION_NOT_SUPPORTED(COMMON),
		WG_STONE(COMMON),

		SLIMY_LOGS(TIC),
		NON_DOUBLE_DOOR(QUARK);

		public final TagKey<Block> tag;

		AllBlockTags() {
			this(MOD);
		}

		AllBlockTags(NameSpace namespace) {
			this(namespace, null);
		}

		AllBlockTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.BLOCK, namespace.id(this, pathOverride));
		}

		public boolean matches(Block block) {
			return block.builtInRegistryHolder().is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack != null && stack.getItem() instanceof BlockItem blockItem && matches(blockItem.getBlock());
		}

		public boolean matches(BlockState state) {
			return state.is(tag);
		}
	}

	public enum AllItemTags {
		BLAZE_BURNER_FUEL_SPECIAL(MOD, "blaze_burner_fuel/special"),
		CASING,
		CONTRAPTION_CONTROLLED,
		CREATE_INGOTS,
		CRUSHED_RAW_MATERIALS,
		INVALID_FOR_TRACK_PAVING,
		DEPLOYABLE_DRINK,
		PRESSURIZED_AIR_SOURCES,
		SANDPAPER,
		SEATS,
		POSTBOXES,
		TABLE_CLOTHS,
		DYED_TABLE_CLOTHS,
		PULPIFIABLE,
		SLEEPERS,
		TOOLBOXES,
		PACKAGES,
		CHAIN_RIDEABLE,
		TRACKS,
		UPRIGHT_ON_BELT,
		NOT_UPRIGHT_ON_BELT,
		NOT_POTION,
		VALVE_HANDLES,
		DISPENSE_BEHAVIOR_WRAP_BLACKLIST,

		OBSIDIAN_DUST(COMMON, "dusts/obsidian"),

		PLATES(COMMON),
		OBSIDIAN_PLATES(COMMON, "plates/obsidian"),
		CARDBOARD_PLATES(COMMON, "plates/cardboard"),

		ALLURITE(MOD, "stone_types/galosphere/allurite"),
		AMETHYST(MOD, "stone_types/galosphere/amethyst"),
		LUMIERE(MOD, "stone_types/galosphere/lumiere"),

		CERTUS_QUARTZ(COMMON, "gems/certus_quartz"),

		AMETRINE_ORES(COMMON, "ores/ametrine"),
		ANTHRACITE_ORES(COMMON, "ores/anthracite"),
		EMERALDITE_ORES(COMMON, "ores/emeraldite"),
		LIGNITE_ORES(COMMON, "ores/lignite"),

		CARDBOARD_STORAGE_BLOCKS(COMMON, "storage_blocks/cardboard"),
		ANDESITE_ALLOY_STORAGE_BLOCKS(COMMON, "storage_blocks/andesite_alloy"),

		CHOCOLATE_BUCKETS(COMMON, "buckets/chocolate"),
		HONEY_BUCKETS(COMMON, "buckets/honey"),

		FOODS_CHOCOLATE(COMMON, "foods/chocolate"),
		FOODS_DOUGH_WHEAT(COMMON, "foods/dough/wheat"),

		DRINKS_TEA(COMMON, "drinks/tea"),

		FLOURS(COMMON),
		WHEAT_FLOURS(COMMON, "flours/wheat");

		public final TagKey<Item> tag;

		AllItemTags() {
			this(MOD);
		}

		AllItemTags(NameSpace namespace) {
			this(namespace, null);
		}

		AllItemTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.ITEM, namespace.id(this, pathOverride));
		}

		public boolean matches(Item item) {
			return item.builtInRegistryHolder().is(tag);
		}

		public boolean matches(ItemStack stack) {
			return stack.is(tag);
		}
	}

	public enum AllFluidTags {
		BOTTOMLESS_ALLOW(MOD, "bottomless/allow"),
		BOTTOMLESS_DENY(MOD, "bottomless/deny"),
		FAN_PROCESSING_CATALYSTS_BLASTING(MOD, "fan_processing_catalysts/blasting"),
		FAN_PROCESSING_CATALYSTS_HAUNTING(MOD, "fan_processing_catalysts/haunting"),
		FAN_PROCESSING_CATALYSTS_SMOKING(MOD, "fan_processing_catalysts/smoking"),
		FAN_PROCESSING_CATALYSTS_SPLASHING(MOD, "fan_processing_catalysts/splashing"),

		TEA(COMMON),
		CHOCOLATE(COMMON),
		CREOSOTE(COMMON);

		public final TagKey<Fluid> tag;

		AllFluidTags() {
			this(MOD);
		}

		AllFluidTags(NameSpace namespace) {
			this(namespace, null);
		}

		AllFluidTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.FLUID, namespace.id(this, pathOverride));
		}

		public boolean matches(Fluid fluid) {
			return fluid.is(tag);
		}

		public boolean matches(FluidState state) {
			return state.is(tag);
		}
	}

	public enum AllEntityTags {
		BLAZE_BURNER_CAPTURABLE,
		IGNORE_SEAT;

		public final TagKey<EntityType<?>> tag;

		AllEntityTags() {
			this(MOD);
		}

		AllEntityTags(NameSpace namespace) {
			this(namespace, null);
		}

		AllEntityTags(NameSpace namespace, @Nullable String pathOverride) {
			this.tag = TagKey.create(Registries.ENTITY_TYPE, namespace.id(this, pathOverride));
		}

		public boolean matches(EntityType<?> type) {
			return type.is(tag);
		}

		public boolean matches(Entity entity) {
			return matches(entity.getType());
		}
	}

	public static void init() {
	}
}

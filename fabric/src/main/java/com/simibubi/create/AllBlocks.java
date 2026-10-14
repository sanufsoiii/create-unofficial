package com.simibubi.create;

import com.simibubi.create.content.decoration.CardboardBlock;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.materials.ExperienceBlock;
import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.chainDrive.ChainDriveBlock;
import com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.GearshiftBlock;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockItem;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelStructuralBlock;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.simibubi.create.infrastructure.config.CStress;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlock;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlock;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteItem;
import com.simibubi.create.content.logistics.chute.SmartChuteBlock;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlock;
import com.simibubi.create.content.kinetics.turntable.TurntableBlock;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.logistics.funnel.AndesiteFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BrassFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.createmod.catnip.data.Couple;

public class AllBlocks {
	private static final CreateRegistrate REGISTRATE = CreateFabric.REGISTRATE;

	static {
		REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
	}

	public static final BlockEntry<CasingBlock> ANDESITE_CASING = REGISTRATE.block("andesite_casing", CasingBlock::new)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.simpleItem()
		.register();

	public static final BlockEntry<CasingBlock> BRASS_CASING = REGISTRATE.block("brass_casing", CasingBlock::new)
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
		.simpleItem()
		.register();

	public static final BlockEntry<CasingBlock> COPPER_CASING = REGISTRATE.block("copper_casing", CasingBlock::new)
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).sound(SoundType.COPPER))
		.simpleItem()
		.register();

	public static final BlockEntry<CasingBlock> RAILWAY_CASING = REGISTRATE.block("railway_casing", CasingBlock::new)
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN))
		.simpleItem()
		.register();

	public static final BlockEntry<CardboardBlock> CARDBOARD_BLOCK = REGISTRATE.block("cardboard_block", CardboardBlock::new)
		.properties(p -> p.mapColor(MapColor.WOOD).sound(SoundType.WOOD))
		.simpleItem()
		.register();

	public static final BlockEntry<ExperienceBlock> EXPERIENCE_BLOCK = REGISTRATE.block("experience_block", ExperienceBlock::new)
		.properties(p -> p.mapColor(MapColor.DIAMOND))
		.simpleItem()
		.register();

	// Ores and Worldgen Palette Stones
	public static final BlockEntry<Block> ZINC_ORE = REGISTRATE.block("zinc_ore", Block::new)
		.initialProperties(() -> Blocks.GOLD_ORE)
		.properties(p -> p.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.sound(SoundType.STONE))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> DEEPSLATE_ZINC_ORE = REGISTRATE.block("deepslate_zinc_ore", Block::new)
		.initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
		.properties(p -> p.mapColor(MapColor.STONE)
			.requiresCorrectToolForDrops()
			.sound(SoundType.DEEPSLATE))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> ASURINE = REGISTRATE.block("asurine", Block::new)
		.initialProperties(() -> Blocks.DEEPSLATE)
		.properties(p -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_BLUE))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> CRIMSITE = REGISTRATE.block("crimsite", Block::new)
		.initialProperties(() -> Blocks.DEEPSLATE)
		.properties(p -> p.destroyTime(1.25f).mapColor(MapColor.COLOR_RED))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> LIMESTONE = REGISTRATE.block("limestone", Block::new)
		.initialProperties(() -> Blocks.SANDSTONE)
		.properties(p -> p.destroyTime(1.25f).mapColor(MapColor.SAND))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> OCHRUM = REGISTRATE.block("ochrum", Block::new)
		.initialProperties(() -> Blocks.CALCITE)
		.properties(p -> p.destroyTime(1.25f).mapColor(MapColor.TERRACOTTA_YELLOW))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> SCORIA = REGISTRATE.block("scoria", Block::new)
		.initialProperties(() -> Blocks.BLACKSTONE)
		.properties(p -> p.mapColor(MapColor.COLOR_BROWN))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> SCORCHIA = REGISTRATE.block("scorchia", Block::new)
		.initialProperties(() -> Blocks.BLACKSTONE)
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_GRAY))
		.simpleItem()
		.register();

	public static final BlockEntry<Block> VERIDIUM = REGISTRATE.block("veridium", Block::new)
		.initialProperties(() -> Blocks.TUFF)
		.properties(p -> p.destroyTime(1.25f).mapColor(MapColor.WARPED_NYLIUM))
		.simpleItem()
		.register();

	// Kinetics

	public static final BlockEntry<ShaftBlock> SHAFT = REGISTRATE.block("shaft", ShaftBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.METAL).forceSolidOff())
		.transform(CStress.setNoImpact())
		.transform(TagGen.pickaxeOnly())
		.simpleItem()
		.register();

	public static final BlockEntry<CogWheelBlock> COGWHEEL = REGISTRATE.block("cogwheel", CogWheelBlock::small)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.DIRT))
		.transform(CStress.setNoImpact())
		.transform(TagGen.axeOrPickaxe())
		.item(CogwheelBlockItem::new)
		.build()
		.register();

	public static final BlockEntry<CogWheelBlock> LARGE_COGWHEEL = REGISTRATE.block("large_cogwheel", CogWheelBlock::large)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.DIRT))
		.transform(CStress.setNoImpact())
		.transform(TagGen.axeOrPickaxe())
		.item(CogwheelBlockItem::new)
		.build()
		.register();

	public static final BlockEntry<EncasedShaftBlock> ANDESITE_ENCASED_SHAFT =
		REGISTRATE.block("andesite_encased_shaft", p -> new EncasedShaftBlock(p, AllBlocks.ANDESITE_CASING::get))
			.properties(p -> p.mapColor(MapColor.PODZOL))
			.transform(BuilderTransformers.encasedShaft("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
			.transform(EncasingRegistry.addVariantTo(AllBlocks.SHAFT))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<EncasedShaftBlock> BRASS_ENCASED_SHAFT =
		REGISTRATE.block("brass_encased_shaft", p -> new EncasedShaftBlock(p, AllBlocks.BRASS_CASING::get))
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
			.transform(BuilderTransformers.encasedShaft("brass", () -> AllSpriteShifts.BRASS_CASING))
			.transform(EncasingRegistry.addVariantTo(AllBlocks.SHAFT))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<EncasedCogwheelBlock> ANDESITE_ENCASED_COGWHEEL = REGISTRATE
		.block("andesite_encased_cogwheel", p -> new EncasedCogwheelBlock(p, false, AllBlocks.ANDESITE_CASING::get))
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(BuilderTransformers.encasedCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
		.transform(EncasingRegistry.addVariantTo(AllBlocks.COGWHEEL))
		.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.ANDESITE_CASING,
			Couple.create(AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_SIDE,
				AllSpriteShifts.ANDESITE_ENCASED_COGWHEEL_OTHERSIDE))))
		.transform(TagGen.axeOrPickaxe())
		.register();

	public static final BlockEntry<EncasedCogwheelBlock> BRASS_ENCASED_COGWHEEL = REGISTRATE
		.block("brass_encased_cogwheel", p -> new EncasedCogwheelBlock(p, false, AllBlocks.BRASS_CASING::get))
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
		.transform(BuilderTransformers.encasedCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))
		.transform(EncasingRegistry.addVariantTo(AllBlocks.COGWHEEL))
		.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(AllSpriteShifts.BRASS_CASING,
			Couple.create(AllSpriteShifts.BRASS_ENCASED_COGWHEEL_SIDE,
				AllSpriteShifts.BRASS_ENCASED_COGWHEEL_OTHERSIDE))))
		.transform(TagGen.axeOrPickaxe())
		.register();

	public static final BlockEntry<EncasedCogwheelBlock> ANDESITE_ENCASED_LARGE_COGWHEEL = REGISTRATE
		.block("andesite_encased_large_cogwheel",
			p -> new EncasedCogwheelBlock(p, true, AllBlocks.ANDESITE_CASING::get))
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(BuilderTransformers.encasedLargeCogwheel("andesite", () -> AllSpriteShifts.ANDESITE_CASING))
		.transform(EncasingRegistry.addVariantTo(AllBlocks.LARGE_COGWHEEL))
		.transform(TagGen.axeOrPickaxe())
		.register();

	public static final BlockEntry<EncasedCogwheelBlock> BRASS_ENCASED_LARGE_COGWHEEL = REGISTRATE
		.block("brass_encased_large_cogwheel",
			p -> new EncasedCogwheelBlock(p, true, AllBlocks.BRASS_CASING::get))
		.properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN))
		.transform(BuilderTransformers.encasedLargeCogwheel("brass", () -> AllSpriteShifts.BRASS_CASING))
		.transform(EncasingRegistry.addVariantTo(AllBlocks.LARGE_COGWHEEL))
		.transform(TagGen.axeOrPickaxe())
		.register();

	public static final BlockEntry<GearboxBlock> GEARBOX = REGISTRATE.block("gearbox", GearboxBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
		.transform(CStress.setNoImpact())
		.transform(TagGen.axeOrPickaxe())
		.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING)))
		.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING,
			(s, f) -> f.getAxis() == s.getValue(GearboxBlock.AXIS))))
		.simpleItem()
		.register();

	public static final BlockEntry<ClutchBlock> CLUTCH = REGISTRATE.block("clutch", ClutchBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
		.transform(CStress.setNoImpact())
		.transform(TagGen.axeOrPickaxe())
		.simpleItem()
		.register();

	public static final BlockEntry<GearshiftBlock> GEARSHIFT = REGISTRATE.block("gearshift", GearshiftBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
		.transform(CStress.setNoImpact())
		.transform(TagGen.axeOrPickaxe())
		.simpleItem()
		.register();

	public static final BlockEntry<ChainDriveBlock> ENCASED_CHAIN_DRIVE =
		REGISTRATE.block("encased_chain_drive", ChainDriveBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
			.transform(CStress.setNoImpact())
			.transform(TagGen.axeOrPickaxe())
			.simpleItem()
			.register();

	public static final BlockEntry<ChainGearshiftBlock> ADJUSTABLE_CHAIN_GEARSHIFT =
		REGISTRATE.block("adjustable_chain_gearshift", ChainGearshiftBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion().mapColor(MapColor.NETHER))
			.transform(CStress.setNoImpact())
			.transform(TagGen.axeOrPickaxe())
			.simpleItem()
			.register();

	public static final BlockEntry<WaterWheelBlock> WATER_WHEEL = REGISTRATE.block("water_wheel", WaterWheelBlock::new)
		.initialProperties(SharedProperties::wooden)
		.properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setCapacity(32))
		.onRegister(BlockStressValues.setGeneratorSpeed(8))
		.simpleItem()
		.register();

	public static final BlockEntry<LargeWaterWheelBlock> LARGE_WATER_WHEEL =
		REGISTRATE.block("large_water_wheel", LargeWaterWheelBlock::new)
			.initialProperties(SharedProperties::wooden)
			.properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setCapacity(128.0))
			.onRegister(BlockStressValues.setGeneratorSpeed(4))
			.item(LargeWaterWheelBlockItem::new)
			.build()
			.register();

	public static final BlockEntry<WaterWheelStructuralBlock> WATER_WHEEL_STRUCTURAL =
		REGISTRATE.block("water_wheel_structure", WaterWheelStructuralBlock::new)
			.initialProperties(SharedProperties::wooden)
			.properties(p -> p.noOcclusion().mapColor(MapColor.DIRT))
			.transform(TagGen.axeOrPickaxe())
			.lang("Large Water Wheel")
			.register();

	public static final BlockEntry<HandCrankBlock> HAND_CRANK = REGISTRATE.block("hand_crank", HandCrankBlock::new)
		.initialProperties(SharedProperties::wooden)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setCapacity(8.0))
		.onRegister(BlockStressValues.setGeneratorSpeed(32))
		.tag(AllBlockTags.BRITTLE.tag)
		.simpleItem()
		.register();

	public static final BlockEntry<DrillBlock> MECHANICAL_DRILL = REGISTRATE.block("mechanical_drill", DrillBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setImpact(4.0))
		.simpleItem()
		.register();

	public static final BlockEntry<SawBlock> MECHANICAL_SAW = REGISTRATE.block("mechanical_saw", SawBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setImpact(4.0))
		.simpleItem()
		.register();

	public static final BlockEntry<MillstoneBlock> MILLSTONE = REGISTRATE.block("millstone", MillstoneBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.METAL))
		.transform(TagGen.pickaxeOnly())
		.transform(CStress.setImpact(4.0))
		.simpleItem()
		.register();

	public static final BlockEntry<CrushingWheelBlock> CRUSHING_WHEEL =
		REGISTRATE.block("crushing_wheel", CrushingWheelBlock::new)
			.properties(p -> p.mapColor(MapColor.METAL))
			.initialProperties(SharedProperties::stone)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(TagGen.pickaxeOnly())
			.transform(CStress.setImpact(8.0))
			.simpleItem()
			.register();

	public static final BlockEntry<CrushingWheelControllerBlock> CRUSHING_WHEEL_CONTROLLER =
		REGISTRATE.block("crushing_wheel_controller", CrushingWheelControllerBlock::new)
			.properties(p -> p.mapColor(MapColor.STONE)
				.noOcclusion()
				.noLootTable()
				.air()
				.noCollission()
				.pushReaction(PushReaction.BLOCK))
			.register();

	public static final BlockEntry<MechanicalPressBlock> MECHANICAL_PRESS =
		REGISTRATE.block("mechanical_press", MechanicalPressBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(8.0))
			.simpleItem()
			.register();

	public static final BlockEntry<MechanicalMixerBlock> MECHANICAL_MIXER =
		REGISTRATE.block("mechanical_mixer", MechanicalMixerBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(4.0))
			.simpleItem()
			.register();

	public static final BlockEntry<GaugeBlock> SPEEDOMETER = REGISTRATE.block("speedometer", GaugeBlock::speed)
		.initialProperties(SharedProperties::wooden)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setNoImpact())
		.simpleItem()
		.register();

	public static final BlockEntry<GaugeBlock> STRESSOMETER = REGISTRATE.block("stressometer", GaugeBlock::stress)
		.initialProperties(SharedProperties::wooden)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setNoImpact())
		.simpleItem()
		.register();

	public static final BlockEntry<CuckooClockBlock> CUCKOO_CLOCK =
		REGISTRATE.block("cuckoo_clock", CuckooClockBlock::regular)
			.initialProperties(SharedProperties::wooden)
			.properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(1.0))
			.simpleItem()
			.register();

	public static final BlockEntry<CuckooClockBlock> MYSTERIOUS_CUCKOO_CLOCK =
		REGISTRATE.block("mysterious_cuckoo_clock", CuckooClockBlock::mysterious)
			.initialProperties(SharedProperties::wooden)
			.properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(1.0))
			.simpleItem()
			.register();

	public static final BlockEntry<TurntableBlock> TURNTABLE = REGISTRATE.block("turntable", TurntableBlock::new)
		.initialProperties(SharedProperties::wooden)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setImpact(4.0))
		.simpleItem()
		.register();

	public static final BlockEntry<CreativeMotorBlock> CREATIVE_MOTOR =
		REGISTRATE.block("creative_motor", CreativeMotorBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.mapColor(MapColor.COLOR_PURPLE).forceSolidOn())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.transform(TagGen.pickaxeOnly())
			.transform(CStress.setCapacity(16384.0))
			.onRegister(BlockStressValues.setGeneratorSpeed(256, true))
			.item()
			.properties(p -> p.rarity(Rarity.EPIC))
			.build()
			.register();

	public static final BlockEntry<SpeedControllerBlock> ROTATION_SPEED_CONTROLLER =
		REGISTRATE.block("rotation_speed_controller", SpeedControllerBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.axeOrPickaxe())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.transform(CStress.setNoImpact())
			.simpleItem()
			.register();

	public static final BlockEntry<DepotBlock> DEPOT = REGISTRATE.block("depot", DepotBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.COLOR_GRAY))
		.transform(TagGen.axeOrPickaxe())
		.simpleItem()
		.register();

	public static final BlockEntry<BasinBlock> BASIN = REGISTRATE.block("basin", BasinBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.COLOR_GRAY)
			.sound(SoundType.NETHERITE_BLOCK))
		.transform(TagGen.pickaxeOnly())
		.simpleItem()
		.register();

	public static final BlockEntry<BlazeBurnerBlock> BLAZE_BURNER =
		REGISTRATE.block("blaze_burner", BlazeBurnerBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.COLOR_GRAY)
				.lightLevel(BlazeBurnerBlock::getLight))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.FAN_PROCESSING_CATALYSTS_BLASTING.tag, AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag,
				AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.PASSIVE_BOILER_HEATERS.tag)
			.item(BlazeBurnerBlockItem::withBlaze)
			.build()
			.register();

	public static final BlockEntry<LitBlazeBurnerBlock> LIT_BLAZE_BURNER =
		REGISTRATE.block("lit_blaze_burner", LitBlazeBurnerBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY)
				.lightLevel(LitBlazeBurnerBlock::getLight))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.FAN_PROCESSING_CATALYSTS_HAUNTING.tag, AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag,
				AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.PASSIVE_BOILER_HEATERS.tag)
			.register();

	public static final BlockEntry<Block> INDUSTRIAL_IRON_BLOCK = REGISTRATE.block("industrial_iron_block", Block::new)
		.initialProperties(() -> Blocks.IRON_BLOCK)
		.properties(p -> p.mapColor(MapColor.COLOR_GRAY))
		.transform(TagGen.pickaxeOnly())
		.simpleItem()
		.register();

	public static final BlockEntry<ChuteBlock> CHUTE = REGISTRATE.block("chute", ChuteBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(p -> p.mapColor(MapColor.COLOR_GRAY)
			.sound(SoundType.NETHERITE_BLOCK)
			.noOcclusion()
			.isSuffocating((state, level, pos) -> false))
		.transform(TagGen.pickaxeOnly())
		.item(ChuteItem::new)
		.build()
		.register();

	public static final BlockEntry<SmartChuteBlock> SMART_CHUTE = REGISTRATE.block("smart_chute", SmartChuteBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(p -> p.mapColor(MapColor.COLOR_GRAY)
			.sound(SoundType.NETHERITE_BLOCK)
			.noOcclusion()
			.isSuffocating((state, level, pos) -> false)
			.isRedstoneConductor((state, level, pos) -> false))
		.transform(TagGen.pickaxeOnly())
		.simpleItem()
		.register();

	public static final BlockEntry<EncasedFanBlock> ENCASED_FAN = REGISTRATE.block("encased_fan", EncasedFanBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.mapColor(MapColor.PODZOL))
		.transform(TagGen.axeOrPickaxe())
		.transform(CStress.setImpact(2.0))
		.simpleItem()
		.register();

	public static final BlockEntry<BeltBlock> BELT = REGISTRATE.block("belt", BeltBlock::new)
		.properties(p -> p.sound(SoundType.WOOL)
			.strength(0.8f)
			.mapColor(MapColor.COLOR_GRAY))
		.register();

	public static final BlockEntry<AndesiteFunnelBlock> ANDESITE_FUNNEL =
		REGISTRATE.block("andesite_funnel", AndesiteFunnelBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.mapColor(MapColor.STONE))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.item(FunnelItem::new)
			.tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.build()
			.register();

	public static final BlockEntry<BeltFunnelBlock> ANDESITE_BELT_FUNNEL =
		REGISTRATE.block("andesite_belt_funnel", p -> new BeltFunnelBlock(AllBlocks.ANDESITE_FUNNEL, p))
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.mapColor(MapColor.STONE))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.loot((p, b) -> p.dropOther(b, ANDESITE_FUNNEL.get()))
			.register();

	public static final BlockEntry<BrassFunnelBlock> BRASS_FUNNEL =
		REGISTRATE.block("brass_funnel", BrassFunnelBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.item(FunnelItem::new)
			.tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.build()
			.register();

	public static final BlockEntry<BeltFunnelBlock> BRASS_BELT_FUNNEL =
		REGISTRATE.block("brass_belt_funnel", p -> new BeltFunnelBlock(AllBlocks.BRASS_FUNNEL, p))
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.pickaxeOnly())
			.tag(AllBlockTags.SAFE_NBT.tag)
			.loot((p, b) -> p.dropOther(b, BRASS_FUNNEL.get()))
			.register();

	public static final BlockEntry<com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock> MECHANICAL_ARM =
		REGISTRATE.block("mechanical_arm", com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock::new)
			.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(2.0))
			.item(com.simibubi.create.content.kinetics.mechanicalArm.ArmItem::new)
			.build()
			.register();

	public static final BlockEntry<com.simibubi.create.content.kinetics.deployer.DeployerBlock> DEPLOYER =
		REGISTRATE.block("deployer", com.simibubi.create.content.kinetics.deployer.DeployerBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.mapColor(MapColor.PODZOL))
			.transform(TagGen.axeOrPickaxe())
			.transform(CStress.setImpact(4.0))
			.item(com.simibubi.create.content.processing.AssemblyOperatorBlockItem::new)
			.tag(AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.build()
			.register();

	public static final BlockEntry<com.simibubi.create.content.fluids.tank.FluidTankBlock> FLUID_TANK = REGISTRATE
		.block("fluid_tank", com.simibubi.create.content.fluids.tank.FluidTankBlock::regular)
		.initialProperties(SharedProperties::copperMetal)
		.properties(p -> p.noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
		.transform(TagGen.pickaxeOnly())
		.item(com.simibubi.create.content.fluids.tank.FluidTankItem::new)
		.build()
		.register();

	public static final BlockEntry<com.simibubi.create.content.fluids.tank.FluidTankBlock> CREATIVE_FLUID_TANK = REGISTRATE
		.block("creative_fluid_tank", com.simibubi.create.content.fluids.tank.FluidTankBlock::creative)
		.initialProperties(SharedProperties::copperMetal)
		.properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_PURPLE))
		.transform(TagGen.pickaxeOnly())
		.tag(AllBlockTags.SAFE_NBT.tag)
		.item(com.simibubi.create.content.fluids.tank.FluidTankItem::new)
		.properties(p -> p.rarity(net.minecraft.world.item.Rarity.EPIC))
		.build()
		.register();

	public static void register() {
	}
}

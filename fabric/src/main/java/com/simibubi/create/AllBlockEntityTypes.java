package com.simibubi.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class AllBlockEntityTypes {
	private static final CreateRegistrate REGISTRATE = CreateFabric.REGISTRATE;

	public static final BlockEntityEntry<BracketedKineticBlockEntity> BRACKETED_KINETIC = REGISTRATE
		.blockEntity("simple_kinetic", BracketedKineticBlockEntity::new)
		.validBlocks(AllBlocks.SHAFT, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL)
		.register();

	public static final BlockEntityEntry<KineticBlockEntity> ENCASED_SHAFT = REGISTRATE
		.blockEntity("encased_shaft", KineticBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_ENCASED_SHAFT, AllBlocks.BRASS_ENCASED_SHAFT, AllBlocks.ENCASED_CHAIN_DRIVE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity> ENCASED_COGWHEEL = REGISTRATE
		.blockEntity("encased_cogwheel", com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_ENCASED_COGWHEEL, AllBlocks.BRASS_ENCASED_COGWHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity> ENCASED_LARGE_COGWHEEL = REGISTRATE
		.blockEntity("encased_large_cogwheel", com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_ENCASED_LARGE_COGWHEEL, AllBlocks.BRASS_ENCASED_LARGE_COGWHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity> GEARBOX = REGISTRATE
		.blockEntity("gearbox", com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity::new)
		.validBlocks(AllBlocks.GEARBOX)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity> CLUTCH = REGISTRATE
		.blockEntity("clutch", com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity::new)
		.validBlocks(AllBlocks.CLUTCH)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.transmission.GearshiftBlockEntity> GEARSHIFT = REGISTRATE
		.blockEntity("gearshift", com.simibubi.create.content.kinetics.transmission.GearshiftBlockEntity::new)
		.validBlocks(AllBlocks.GEARSHIFT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlockEntity> ADJUSTABLE_CHAIN_GEARSHIFT = REGISTRATE
		.blockEntity("adjustable_chain_gearshift", com.simibubi.create.content.kinetics.chainDrive.ChainGearshiftBlockEntity::new)
		.validBlocks(AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity> WATER_WHEEL = REGISTRATE
		.blockEntity("water_wheel", com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity::new)
		.validBlocks(AllBlocks.WATER_WHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockEntity> LARGE_WATER_WHEEL = REGISTRATE
		.blockEntity("large_water_wheel", com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlockEntity::new)
		.validBlocks(AllBlocks.LARGE_WATER_WHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity> HAND_CRANK = REGISTRATE
		.blockEntity("hand_crank", com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity::new)
		.validBlocks(AllBlocks.HAND_CRANK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.drill.DrillBlockEntity> DRILL = REGISTRATE
		.blockEntity("drill", com.simibubi.create.content.kinetics.drill.DrillBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_DRILL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.saw.SawBlockEntity> SAW = REGISTRATE
		.blockEntity("saw", com.simibubi.create.content.kinetics.saw.SawBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_SAW)
		.register();

	public static void register() {
	}
}

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
		.validBlocks(AllBlocks.ANDESITE_ENCASED_SHAFT, AllBlocks.BRASS_ENCASED_SHAFT, AllBlocks.ENCASED_CHAIN_DRIVE, AllBlocks.METAL_GIRDER_ENCASED_SHAFT)
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

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity> SEQUENCED_GEARSHIFT = REGISTRATE
		.blockEntity("sequenced_gearshift", com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity::new)
		.validBlocks(AllBlocks.SEQUENCED_GEARSHIFT)
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

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity> VALVE_HANDLE = REGISTRATE
		.blockEntity("valve_handle", com.simibubi.create.content.kinetics.crank.ValveHandleBlockEntity::new)
		.validBlocks(AllBlocks.COPPER_VALVE_HANDLE)
		.validBlocks(AllBlocks.DYED_VALVE_HANDLES.toArray())
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.drill.DrillBlockEntity> DRILL = REGISTRATE
		.blockEntity("drill", com.simibubi.create.content.kinetics.drill.DrillBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_DRILL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.saw.SawBlockEntity> SAW = REGISTRATE
		.blockEntity("saw", com.simibubi.create.content.kinetics.saw.SawBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_SAW)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity> MILLSTONE = REGISTRATE
		.blockEntity("millstone", com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity::new)
		.validBlocks(AllBlocks.MILLSTONE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity> CRUSHING_WHEEL = REGISTRATE
		.blockEntity("crushing_wheel", com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity::new)
		.validBlocks(AllBlocks.CRUSHING_WHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity> CRUSHING_WHEEL_CONTROLLER = REGISTRATE
		.blockEntity("crushing_wheel_controller", com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity::new)
		.validBlocks(AllBlocks.CRUSHING_WHEEL_CONTROLLER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity> MECHANICAL_PRESS = REGISTRATE
		.blockEntity("mechanical_press", com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PRESS)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity> MECHANICAL_MIXER = REGISTRATE
		.blockEntity("mechanical_mixer", com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_MIXER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity> SPEEDOMETER = REGISTRATE
		.blockEntity("speedometer", com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity::new)
		.validBlocks(AllBlocks.SPEEDOMETER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity> STRESSOMETER = REGISTRATE
		.blockEntity("stressometer", com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity::new)
		.validBlocks(AllBlocks.STRESSOMETER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity> CUCKOO_CLOCK = REGISTRATE
		.blockEntity("cuckoo_clock", com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity::new)
		.validBlocks(AllBlocks.CUCKOO_CLOCK, AllBlocks.MYSTERIOUS_CUCKOO_CLOCK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity> TURNTABLE = REGISTRATE
		.blockEntity("turntable", com.simibubi.create.content.kinetics.turntable.TurntableBlockEntity::new)
		.validBlocks(AllBlocks.TURNTABLE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity> MOTOR = REGISTRATE
		.blockEntity("motor", com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_MOTOR)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity> ROTATION_SPEED_CONTROLLER = REGISTRATE
		.blockEntity("rotation_speed_controller", com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity::new)
		.validBlocks(AllBlocks.ROTATION_SPEED_CONTROLLER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.depot.DepotBlockEntity> DEPOT = REGISTRATE
		.blockEntity("depot", com.simibubi.create.content.logistics.depot.DepotBlockEntity::new)
		.validBlocks(AllBlocks.DEPOT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.processing.basin.BasinBlockEntity> BASIN = REGISTRATE
		.blockEntity("basin", com.simibubi.create.content.processing.basin.BasinBlockEntity::new)
		.validBlocks(AllBlocks.BASIN)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity> HEATER = REGISTRATE
		.blockEntity("blaze_heater", com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity::new)
		.validBlocks(AllBlocks.BLAZE_BURNER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.chute.ChuteBlockEntity> CHUTE = REGISTRATE
		.blockEntity("chute", com.simibubi.create.content.logistics.chute.ChuteBlockEntity::new)
		.validBlocks(AllBlocks.CHUTE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity> SMART_CHUTE = REGISTRATE
		.blockEntity("smart_chute", com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity::new)
		.validBlocks(AllBlocks.SMART_CHUTE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity> ENCASED_FAN = REGISTRATE
		.blockEntity("encased_fan", com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity::new)
		.validBlocks(AllBlocks.ENCASED_FAN)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.fan.NozzleBlockEntity> NOZZLE = REGISTRATE
		.blockEntity("nozzle", com.simibubi.create.content.kinetics.fan.NozzleBlockEntity::new)
		.validBlocks(AllBlocks.NOZZLE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.funnel.FunnelBlockEntity> FUNNEL = REGISTRATE
		.blockEntity("funnel", com.simibubi.create.content.logistics.funnel.FunnelBlockEntity::new)
		.validBlocks(AllBlocks.BRASS_FUNNEL, AllBlocks.BRASS_BELT_FUNNEL, AllBlocks.ANDESITE_FUNNEL,
			AllBlocks.ANDESITE_BELT_FUNNEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity> MECHANICAL_ARM = REGISTRATE
		.blockEntity("mechanical_arm", com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_ARM)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity> DEPLOYER = REGISTRATE
		.blockEntity("deployer", com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity::new)
		.validBlocks(AllBlocks.DEPLOYER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.tank.FluidTankBlockEntity> FLUID_TANK = REGISTRATE
		.blockEntity("fluid_tank", com.simibubi.create.content.fluids.tank.FluidTankBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_TANK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity> CREATIVE_FLUID_TANK = REGISTRATE
		.blockEntity("creative_fluid_tank", com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_FLUID_TANK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pump.PumpBlockEntity> MECHANICAL_PUMP = REGISTRATE
		.blockEntity("mechanical_pump", com.simibubi.create.content.fluids.pump.PumpBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PUMP)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity> FLUID_PIPE = REGISTRATE
		.blockEntity("fluid_pipe", com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_PIPE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity> ENCASED_FLUID_PIPE = REGISTRATE
		.blockEntity("encased_fluid_pipe", com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.ENCASED_FLUID_PIPE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity> GLASS_FLUID_PIPE = REGISTRATE
		.blockEntity("glass_fluid_pipe", com.simibubi.create.content.fluids.pipes.StraightPipeBlockEntity::new)
		.validBlocks(AllBlocks.GLASS_FLUID_PIPE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity> SMART_FLUID_PIPE = REGISTRATE
		.blockEntity("smart_fluid_pipe", com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.SMART_FLUID_PIPE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity> ITEM_DRAIN = REGISTRATE
		.blockEntity("item_drain", com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity::new)
		.validBlocks(AllBlocks.ITEM_DRAIN)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.spout.SpoutBlockEntity> SPOUT = REGISTRATE
		.blockEntity("spout", com.simibubi.create.content.fluids.spout.SpoutBlockEntity::new)
		.validBlocks(AllBlocks.SPOUT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity> FLUID_VALVE = REGISTRATE
		.blockEntity("fluid_valve", com.simibubi.create.content.fluids.pipes.valve.FluidValveBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_VALVE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity> HOSE_PULLEY = REGISTRATE
		.blockEntity("hose_pulley", com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity::new)
		.validBlocks(AllBlocks.HOSE_PULLEY)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.decoration.steamWhistle.WhistleBlockEntity> STEAM_WHISTLE = REGISTRATE
		.blockEntity("steam_whistle", com.simibubi.create.content.decoration.steamWhistle.WhistleBlockEntity::new)
		.validBlocks(AllBlocks.STEAM_WHISTLE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.decoration.placard.PlacardBlockEntity> PLACARD = REGISTRATE
		.blockEntity("placard", com.simibubi.create.content.decoration.placard.PlacardBlockEntity::new)
		.validBlocks(AllBlocks.PLACARD)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity> CREATIVE_CRATE = REGISTRATE
		.blockEntity("creative_crate", com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_CRATE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity> ITEM_VAULT = REGISTRATE
		.blockEntity("item_vault", com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity::new)
		.validBlocks(AllBlocks.ITEM_VAULT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity> ANALOG_LEVER = REGISTRATE
		.blockEntity("analog_lever", com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity::new)
		.validBlocks(AllBlocks.ANALOG_LEVER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.diodes.PulseRepeaterBlockEntity> PULSE_REPEATER = REGISTRATE
		.blockEntity("pulse_repeater", com.simibubi.create.content.redstone.diodes.PulseRepeaterBlockEntity::new)
		.validBlocks(AllBlocks.PULSE_REPEATER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.diodes.PulseExtenderBlockEntity> PULSE_EXTENDER = REGISTRATE
		.blockEntity("pulse_extender", com.simibubi.create.content.redstone.diodes.PulseExtenderBlockEntity::new)
		.validBlocks(AllBlocks.PULSE_EXTENDER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.diodes.PulseTimerBlockEntity> PULSE_TIMER = REGISTRATE
		.blockEntity("pulse_timer", com.simibubi.create.content.redstone.diodes.PulseTimerBlockEntity::new)
		.validBlocks(AllBlocks.PULSE_TIMER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity> STEAM_ENGINE = REGISTRATE
		.blockEntity("steam_engine", com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity::new)
		.validBlocks(AllBlocks.STEAM_ENGINE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity> POWERED_SHAFT = REGISTRATE
		.blockEntity("powered_shaft", com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity::new)
		.validBlocks(AllBlocks.POWERED_SHAFT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity> FLYWHEEL = REGISTRATE
		.blockEntity("flywheel", com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity::new)
		.validBlocks(AllBlocks.FLYWHEEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.deskBell.DeskBellBlockEntity> DESK_BELL = REGISTRATE
		.blockEntity("desk_bell", com.simibubi.create.content.redstone.deskBell.DeskBellBlockEntity::new)
		.validBlocks(AllBlocks.DESK_BELL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.equipment.bell.PeculiarBellBlockEntity> PECULIAR_BELL = REGISTRATE
		.blockEntity("peculiar_bell", com.simibubi.create.content.equipment.bell.PeculiarBellBlockEntity::new)
		.validBlocks(AllBlocks.PECULIAR_BELL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.equipment.bell.HauntedBellBlockEntity> HAUNTED_BELL = REGISTRATE
		.blockEntity("haunted_bell", com.simibubi.create.content.equipment.bell.HauntedBellBlockEntity::new)
		.validBlocks(AllBlocks.HAUNTED_BELL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity> SLIDING_DOOR =
		REGISTRATE.blockEntity("sliding_door", com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity::new)
			.validBlocks(AllBlocks.TRAIN_DOOR, AllBlocks.FRAMED_GLASS_DOOR, AllBlocks.ANDESITE_DOOR,
				AllBlocks.BRASS_DOOR, AllBlocks.COPPER_DOOR)
			.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity> CHASSIS = REGISTRATE
		.blockEntity("chassis", com.simibubi.create.content.contraptions.chassis.ChassisBlockEntity::new)
		.validBlocks(AllBlocks.RADIAL_CHASSIS, AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.chassis.StickerBlockEntity> STICKER = REGISTRATE
		.blockEntity("sticker", com.simibubi.create.content.contraptions.chassis.StickerBlockEntity::new)
		.validBlocks(AllBlocks.STICKER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity> NIXIE_TUBE = REGISTRATE
		.blockEntity("nixie_tube", com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity::new)
		.validBlocks(AllBlocks.ORANGE_NIXIE_TUBE)
		.validBlocks(AllBlocks.NIXIE_TUBES.toArray())
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity> REDSTONE_LINK = REGISTRATE
		.blockEntity("redstone_link", com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity::new)
		.validBlocks(AllBlocks.REDSTONE_LINK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity> THRESHOLD_SWITCH = REGISTRATE
		.blockEntity("stockpile_switch", com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchBlockEntity::new)
		.validBlocks(AllBlocks.THRESHOLD_SWITCH)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity> SMART_OBSERVER = REGISTRATE
		.blockEntity("content_observer", com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity::new)
		.validBlocks(AllBlocks.SMART_OBSERVER)
		.register();

	public static void register() {
	}
}

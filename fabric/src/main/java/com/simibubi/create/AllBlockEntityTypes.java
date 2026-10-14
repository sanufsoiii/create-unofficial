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

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity> MECHANICAL_PISTON = REGISTRATE
		.blockEntity("mechanical_piston", com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PISTON, AllBlocks.STICKY_MECHANICAL_PISTON)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity> WINDMILL_BEARING = REGISTRATE
		.blockEntity("windmill_bearing", com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity::new)
		.validBlocks(AllBlocks.WINDMILL_BEARING)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity> MECHANICAL_BEARING = REGISTRATE
		.blockEntity("mechanical_bearing", com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_BEARING)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity> CLOCKWORK_BEARING = REGISTRATE
		.blockEntity("clockwork_bearing", com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity::new)
		.validBlocks(AllBlocks.CLOCKWORK_BEARING)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity> ROPE_PULLEY = REGISTRATE
		.blockEntity("rope_pulley", com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity::new)
		.validBlocks(AllBlocks.ROPE_PULLEY)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity> ELEVATOR_PULLEY = REGISTRATE
		.blockEntity("elevator_pulley", com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity::new)
		.validBlocks(AllBlocks.ELEVATOR_PULLEY)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity> GANTRY_SHAFT = REGISTRATE
		.blockEntity("gantry_shaft", com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity::new)
		.validBlocks(AllBlocks.GANTRY_SHAFT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity> GANTRY_PINION = REGISTRATE
		.blockEntity("gantry_pinion", com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity::new)
		.validBlocks(AllBlocks.GANTRY_CARRIAGE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity> CART_ASSEMBLER = REGISTRATE
		.blockEntity("cart_assembler", com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockEntity::new)
		.validBlocks(AllBlocks.CART_ASSEMBLER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity> HARVESTER = REGISTRATE
		.blockEntity("harvester", com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_HARVESTER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity> MECHANICAL_ROLLER = REGISTRATE
		.blockEntity("mechanical_roller", com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_ROLLER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity> CONTRAPTION_CONTROLS = REGISTRATE
		.blockEntity("contraption_controls", com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity::new)
		.validBlocks(AllBlocks.CONTRAPTION_CONTROLS)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.actors.psi.PortableItemInterfaceBlockEntity> PORTABLE_STORAGE_INTERFACE = REGISTRATE
		.blockEntity("portable_storage_interface", com.simibubi.create.content.contraptions.actors.psi.PortableItemInterfaceBlockEntity::new)
		.validBlocks(AllBlocks.PORTABLE_STORAGE_INTERFACE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity> PORTABLE_FLUID_INTERFACE = REGISTRATE
		.blockEntity("portable_fluid_interface", com.simibubi.create.content.contraptions.actors.psi.PortableFluidInterfaceBlockEntity::new)
		.validBlocks(AllBlocks.PORTABLE_FLUID_INTERFACE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.packager.PackagerBlockEntity> PACKAGER = REGISTRATE
		.blockEntity("packager", com.simibubi.create.content.logistics.packager.PackagerBlockEntity::new)
		.validBlocks(AllBlocks.PACKAGER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity> REPACKAGER = REGISTRATE
		.blockEntity("repackager", com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity::new)
		.validBlocks(AllBlocks.REPACKAGER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity> PACKAGE_FROGPORT = REGISTRATE
		.blockEntity("package_frogport", com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity::new)
		.validBlocks(AllBlocks.PACKAGE_FROGPORT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity> PACKAGE_POSTBOX = REGISTRATE
		.blockEntity("package_postbox", com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity::new)
		.validBlocks(AllBlocks.PACKAGE_POSTBOXES.toArray())
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity> CHAIN_CONVEYOR = REGISTRATE
		.blockEntity("chain_conveyor", com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity::new)
		.validBlocks(AllBlocks.CHAIN_CONVEYOR)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity> PACKAGER_LINK = REGISTRATE
		.blockEntity("packager_link", com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity::new)
		.validBlocks(AllBlocks.STOCK_LINK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity> STOCK_TICKER = REGISTRATE
		.blockEntity("stock_ticker", com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity::new)
		.validBlocks(AllBlocks.STOCK_TICKER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity> REDSTONE_REQUESTER = REGISTRATE
		.blockEntity("redstone_requester", com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity::new)
		.validBlocks(AllBlocks.REDSTONE_REQUESTER)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity> FACTORY_PANEL = REGISTRATE
		.blockEntity("factory_panel", com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity::new)
		.validBlocks(AllBlocks.FACTORY_GAUGE)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity> DISPLAY_LINK = REGISTRATE
		.blockEntity("display_link", com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity::new)
		.validBlocks(AllBlocks.DISPLAY_LINK)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.trains.display.FlapDisplayBlockEntity> FLAP_DISPLAY = REGISTRATE
		.blockEntity("flap_display", com.simibubi.create.content.trains.display.FlapDisplayBlockEntity::new)
		.validBlocks(AllBlocks.DISPLAY_BOARD)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity> ELEVATOR_CONTACT = REGISTRATE
		.blockEntity("elevator_contact", com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity::new)
		.validBlocks(AllBlocks.ELEVATOR_CONTACT)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.decoration.copycat.CopycatBlockEntity> COPYCAT = REGISTRATE
		.blockEntity("copycat", com.simibubi.create.content.decoration.copycat.CopycatBlockEntity::new)
		.validBlocks(AllBlocks.COPYCAT_PANEL, AllBlocks.COPYCAT_STEP)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity> ANDESITE_TUNNEL = REGISTRATE
		.blockEntity("andesite_tunnel", com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_TUNNEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity> BRASS_TUNNEL = REGISTRATE
		.blockEntity("brass_tunnel", com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity::new)
		.validBlocks(AllBlocks.BRASS_TUNNEL)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity> TABLE_CLOTH = REGISTRATE
		.blockEntity("table_cloth", com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity::new)
		.validBlocks(AllBlocks.TABLE_CLOTHS.toArray())
		.validBlock(AllBlocks.ANDESITE_TABLE_CLOTH)
		.validBlock(AllBlocks.BRASS_TABLE_CLOTH)
		.validBlock(AllBlocks.COPPER_TABLE_CLOTH)
		.register();

	public static final BlockEntityEntry<com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity> TOOLBOX = REGISTRATE
		.blockEntity("toolbox", com.simibubi.create.content.equipment.toolbox.ToolboxBlockEntity::new)
		.validBlocks(AllBlocks.TOOLBOXES.toArray())
		.register();

	public static void register() {
	}
}

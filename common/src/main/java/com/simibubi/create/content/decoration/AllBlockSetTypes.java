package com.simibubi.create.content.decoration;

import java.util.function.Supplier;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class AllBlockSetTypes {

	public static final Supplier<BlockSetType> TRAIN_SET_TYPE =
		() -> new BlockSetType("create:train", true, true, true,
			BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.NETHERITE_BLOCK, SoundEvents.IRON_DOOR_CLOSE,
			SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
			SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);

	public static final Supplier<BlockSetType> GLASS_SET_TYPE =
		() -> new BlockSetType("create:glass", true, true, true,
			BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.GLASS, SoundEvents.IRON_DOOR_CLOSE,
			SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
			SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);

	public static final Supplier<BlockSetType> STONE_SET_TYPE =
		() -> new BlockSetType("create:stone", true, true, true,
			BlockSetType.PressurePlateSensitivity.EVERYTHING, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE,
			SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
			SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
			SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON);

}

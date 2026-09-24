package com.simibubi.create.api.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BoilerHeaterDefaults {
	public static BoilerHeater passiveHeater = (level, pos, state) -> BoilerHeater.NO_HEAT;
	public static BoilerHeater blazeBurnerHeater = (level, pos, state) -> BoilerHeater.NO_HEAT;

	public static float passive(Level level, BlockPos pos, BlockState state) {
		return passiveHeater.getHeat(level, pos, state);
	}

	public static float blazeBurner(Level level, BlockPos pos, BlockState state) {
		return blazeBurnerHeater.getHeat(level, pos, state);
	}
}

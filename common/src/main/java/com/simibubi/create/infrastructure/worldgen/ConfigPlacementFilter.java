package com.simibubi.create.infrastructure.worldgen;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.utility.PlatformDispatch;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConfigPlacementFilter extends PlacementFilter {
	public static final ConfigPlacementFilter INSTANCE = new ConfigPlacementFilter();
	public static final MapCodec<ConfigPlacementFilter> CODEC = MapCodec.unit(() -> INSTANCE);

	@Override
	protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
		return !isWorldGenDisabled();
	}

	private static boolean isWorldGenDisabled() {
		try {
			Object configs = PlatformDispatch.call(
				"com.simibubi.create.infrastructure.config.AllConfigs",
				"com.simibubi.create.infrastructure.config.AllConfigs",
				"common",
				new Class<?>[0]
			);
			if (configs != null) {
				Object worldGen = configs.getClass().getField("worldGen").get(configs);
				Object disable = worldGen.getClass().getField("disable").get(worldGen);
				return (Boolean) disable.getClass().getMethod("get").invoke(disable);
			}
		} catch (Throwable ignored) {
		}
		return false;
	}

	@Override
	public PlacementModifierType<?> type() {
		return AllPlacementModifiers.CONFIG_FILTER.get();
	}
}

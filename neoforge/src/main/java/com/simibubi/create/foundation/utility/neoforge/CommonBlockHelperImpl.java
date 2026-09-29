package com.simibubi.create.foundation.utility.neoforge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class CommonBlockHelperImpl {

	public static BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
		@Nullable BlockState querySource, @Nullable BlockPos queryPos) {
		return state.getAppearance(level, pos, side, querySource, queryPos);
	}

}

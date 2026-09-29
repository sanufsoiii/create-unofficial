package com.simibubi.create.content.equipment.wrench.fabric;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchHelperImpl {

	public static void switchToBlockState(Level level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state, Block.UPDATE_ALL);
	}

	public static boolean postSneakWrenchBreakEvent(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
		return false;
	}

}

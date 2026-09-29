package com.simibubi.create.content.equipment.wrench;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchHelper {

	@ExpectPlatform
	public static void switchToBlockState(Level level, BlockPos pos, BlockState state) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static boolean postSneakWrenchBreakEvent(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
		throw new AssertionError("@ExpectPlatform");
	}

}

package com.simibubi.create.content.equipment.wrench;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.PlatformDispatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchHelper {

	public static void switchToBlockState(Level level, BlockPos pos, BlockState state) {
		PlatformDispatch.call(
			"com.simibubi.create.content.equipment.wrench.neoforge.WrenchHelperImpl",
			"com.simibubi.create.content.equipment.wrench.fabric.WrenchHelperImpl",
			"switchToBlockState",
			new Class<?>[]{Level.class, BlockPos.class, BlockState.class},
			level, pos, state
		);
	}

	public static boolean postSneakWrenchBreakEvent(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
		return (boolean) PlatformDispatch.call(
			"com.simibubi.create.content.equipment.wrench.neoforge.WrenchHelperImpl",
			"com.simibubi.create.content.equipment.wrench.fabric.WrenchHelperImpl",
			"postSneakWrenchBreakEvent",
			new Class<?>[]{Level.class, BlockPos.class, BlockState.class, Player.class},
			level, pos, state, player
		);
	}

}

package com.simibubi.create.content.equipment.wrench.neoforge;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class WrenchHelperImpl {

	public static void switchToBlockState(Level level, BlockPos pos, BlockState state) {
		KineticBlockEntity.switchToBlockState(level, pos, state);
	}

	public static boolean postSneakWrenchBreakEvent(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
		BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
		NeoForge.EVENT_BUS.post(event);
		return event.isCanceled();
	}

}

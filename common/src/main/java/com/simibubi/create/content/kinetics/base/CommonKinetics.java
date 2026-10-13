package com.simibubi.create.content.kinetics.base;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.PlatformDispatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CommonKinetics {

	public static void onBlockPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.base.neoforge.CommonKineticsImpl",
			"com.simibubi.create.content.kinetics.base.fabric.CommonKineticsImpl",
			"onBlockPlace",
			new Class<?>[]{BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class},
			state, worldIn, pos, oldState, isMoving
		);
	}

	public static void onBlockRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.base.neoforge.CommonKineticsImpl",
			"com.simibubi.create.content.kinetics.base.fabric.CommonKineticsImpl",
			"onBlockRemove",
			new Class<?>[]{BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class},
			pState, pLevel, pPos, pNewState, pIsMoving
		);
	}

	public static void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
		PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.base.neoforge.CommonKineticsImpl",
			"com.simibubi.create.content.kinetics.base.fabric.CommonKineticsImpl",
			"updateIndirectNeighbourShapes",
			new Class<?>[]{BlockState.class, LevelAccessor.class, BlockPos.class, int.class, int.class},
			stateIn, worldIn, pos, flags, count
		);
	}

	public static void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		PlatformDispatch.call(
			"com.simibubi.create.content.kinetics.base.neoforge.CommonKineticsImpl",
			"com.simibubi.create.content.kinetics.base.fabric.CommonKineticsImpl",
			"setPlacedBy",
			new Class<?>[]{Level.class, BlockPos.class, BlockState.class, LivingEntity.class, ItemStack.class},
			worldIn, pos, state, placer, stack
		);
	}

}

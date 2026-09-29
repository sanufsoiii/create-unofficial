package com.simibubi.create.content.kinetics.base;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CommonKinetics {

	@ExpectPlatform
	public static void onBlockPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static void onBlockRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
		throw new AssertionError("@ExpectPlatform");
	}

	@ExpectPlatform
	public static void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		throw new AssertionError("@ExpectPlatform");
	}

}

package com.simibubi.create.content.kinetics.base.neoforge;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CommonKineticsImpl {

	public static void onBlockPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity instanceof KineticBlockEntity kineticBlockEntity) {
			kineticBlockEntity.preventSpeedUpdate = 0;

			if (oldState.getBlock() != state.getBlock())
				return;
			if (state.hasBlockEntity() != oldState.hasBlockEntity())
				return;
			if (state.getBlock() instanceof KineticBlock kb && !kb.areStatesKineticallyEquivalentPublic(oldState, state))
				return;

			kineticBlockEntity.preventSpeedUpdate = 2;
		}
	}

	public static void onBlockRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	public static void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags, int count) {
		if (worldIn.isClientSide())
			return;

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity kbe))
			return;

		if (kbe.preventSpeedUpdate > 0)
			return;

		// Remove previous information when block is added
		kbe.warnOfMovement();
		kbe.clearKineticInformation();
		kbe.updateSpeed = true;
	}

	public static void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer != null)
			AdvancementBehaviour.setPlacedBy(worldIn, pos, placer);
		if (worldIn.isClientSide)
			return;

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity kbe))
			return;

		kbe.queueRotationIndicators();
	}

}

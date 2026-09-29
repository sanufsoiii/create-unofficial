package com.simibubi.create.content.kinetics.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class KineticBlock extends Block implements IRotate {

	public KineticBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		CommonKinetics.onBlockPlace(state, worldIn, pos, oldState, isMoving);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		CommonKinetics.onBlockRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		if (oldState.getBlock() != newState.getBlock())
			return false;
		return getRotationAxis(newState) == getRotationAxis(oldState);
	}

	public boolean areStatesKineticallyEquivalentPublic(BlockState oldState, BlockState newState) {
		return areStatesKineticallyEquivalent(oldState, newState);
	}

	@Override
	public void updateIndirectNeighbourShapes(BlockState stateIn, LevelAccessor worldIn, BlockPos pos, int flags,
											  int count) {
		CommonKinetics.updateIndirectNeighbourShapes(stateIn, worldIn, pos, flags, count);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		CommonKinetics.setPlacedBy(worldIn, pos, state, placer, stack);
	}

	public float getParticleTargetRadius() {
		return .65f;
	}

	public float getParticleInitialRadius() {
		return .75f;
	}

}

package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BeltBlockEntity extends KineticBlockEntity {

	public BlockPos controller;
	public int beltLength = 1;
	public int index;
	public CasingType casing;

	public enum CasingType {
		NONE, ANDESITE, BRASS;
	}

	public BeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		casing = CasingType.NONE;
	}

	public Direction getMovementFacing() {
		return getBlockState().getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(Direction.NORTH);
	}

	public Direction getBeltFacing() {
		return getMovementFacing();
	}

	public float getBeltMovementSpeed() {
		return getSpeed() / 480f;
	}

	public float getDirectionAwareBeltMovementSpeed() {
		int offset = getBeltFacing().getAxisDirection()
			.getStep();
		if (getBeltFacing().getAxis() == Axis.X)
			offset *= -1;
		return getBeltMovementSpeed() * offset;
	}

	public BeltBlockEntity getControllerBE() {
		return this;
	}

	public void setCasingType(CasingType type) {
		if (casing == type)
			return;

		casing = type;
		BlockState blockState = getBlockState();
		boolean shouldBlockHaveCasing = type != CasingType.NONE;

		if (level != null && !level.isClientSide) {
			level.setBlock(worldPosition, blockState.setValue(BeltBlock.CASING, shouldBlockHaveCasing), 2);
			notifyUpdate();
		}
	}
}

package com.simibubi.create.content.kinetics.belt;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BeltBlock extends HorizontalKineticBlock {

	public static final EnumProperty<BeltSlope> SLOPE = EnumProperty.create("slope", BeltSlope.class);
	public static final EnumProperty<BeltPart> PART = EnumProperty.create("part", BeltPart.class);
	public static final BooleanProperty CASING = BooleanProperty.create("casing");

	public BeltBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
			.setValue(SLOPE, BeltSlope.HORIZONTAL)
			.setValue(PART, BeltPart.START)
			.setValue(CASING, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(SLOPE, PART, CASING));
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		if (state.getValue(SLOPE) == BeltSlope.SIDEWAYS)
			return Axis.Y;
		return state.getValue(HORIZONTAL_FACING)
			.getClockWise()
			.getAxis();
	}

	public static boolean canTransportObjects(BlockState state) {
		return !state.getValue(CASING) && state.getValue(SLOPE) != BeltSlope.VERTICAL;
	}

	public static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward) {
		Direction direction = state.getValue(HORIZONTAL_FACING);
		BeltSlope slope = state.getValue(SLOPE);
		BeltPart part = state.getValue(PART);

		int offset = forward ? 1 : -1;

		if (part == BeltPart.END && forward || part == BeltPart.START && !forward)
			return null;
		if (slope == BeltSlope.VERTICAL)
			return pos.above(direction.getAxisDirection() == AxisDirection.POSITIVE ? offset : -offset);
		pos = pos.relative(direction, offset);
		if (slope != BeltSlope.HORIZONTAL && slope != BeltSlope.SIDEWAYS)
			return pos.above(slope == BeltSlope.UPWARD ? offset : -offset);
		return pos;
	}
}

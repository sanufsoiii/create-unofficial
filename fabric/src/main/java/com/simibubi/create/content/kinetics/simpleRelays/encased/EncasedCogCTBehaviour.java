package com.simibubi.create.content.kinetics.simpleRelays.encased;

import static com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock.AXIS;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;

import net.createmod.catnip.data.Couple;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class EncasedCogCTBehaviour extends EncasedCTBehaviour {

	private Couple<CTSpriteShiftEntry> sideShifts;
	private boolean large;

	public EncasedCogCTBehaviour(CTSpriteShiftEntry shift) {
		this(shift, null);
	}

	public EncasedCogCTBehaviour(CTSpriteShiftEntry shift, Couple<CTSpriteShiftEntry> sideShifts) {
		super(shift);
		large = sideShifts == null;
		this.sideShifts = sideShifts;
	}

	@Override
	public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
		BlockPos otherPos, Direction face) {
		Axis axis = state.getValue(AXIS);
		if (large || axis == face.getAxis())
			return super.connectsTo(state, other, reader, pos, otherPos, face);

		if (other.getBlock() == state.getBlock() && other.getValue(AXIS) == state.getValue(AXIS))
			return true;

		BlockState blockState = reader.getBlockState(otherPos.relative(face));
		if (!ICogWheel.isLargeCog(blockState))
			return false;

		return ((IRotate) blockState.getBlock()).getRotationAxis(blockState) == axis;
	}

	@Override
	protected boolean reverseUVs(BlockState state, Direction face) {
		return state.getValue(AXIS)
			.isHorizontal()
			&& face.getAxis()
				.isHorizontal()
			&& face.getAxisDirection() == AxisDirection.POSITIVE;
	}

	@Override
	public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
		if (sideShifts != null && direction.getAxis() != state.getValue(AXIS)) {
			Axis axis = state.getValue(AXIS);
			Axis normal = direction.getAxis();
			boolean top = axis.isHorizontal() && normal == Axis.Y || axis == Axis.Y && normal == Axis.X;
			return top ? sideShifts.getFirst() : sideShifts.getSecond();
		}
		return super.getShift(state, direction, sprite);
	}
}

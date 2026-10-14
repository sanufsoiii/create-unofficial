package com.simibubi.create.content.contraptions.gantry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class GantryCarriageBlockEntity extends KineticBlockEntity implements IDisplayAssemblyExceptions {

	public boolean assembleNextTick;
	protected AssemblyException lastException;

	public GantryCarriageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	public void checkValidGantryShaft() {
		if (shouldAssemble())
			queueAssembly();
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!getBlockState().canSurvive(level, worldPosition))
			level.destroyBlock(worldPosition, true);
	}

	public void queueAssembly() {
		assembleNextTick = true;
	}

	@Override
	public void tick() {
		super.tick();

		if (level.isClientSide)
			return;

		if (assembleNextTick) {
			tryAssemble();
			assembleNextTick = false;
		}
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	private void tryAssemble() {
		// Stage 1 stub: full contraption assembly belongs to Stage 2
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		AssemblyException.write(compound, registries, lastException);
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		lastException = AssemblyException.read(compound, registries);
		super.read(compound, registries, clientPacket);
	}

	public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,
		boolean connectedViaAxes, boolean connectedViaCogs) {
		float defaultModifier = 1.0f;

		if (connectedViaAxes)
			return defaultModifier;
		if (!AllBlocks.GANTRY_SHAFT.has(stateTo))
			return defaultModifier;
		if (!stateTo.getValue(GantryShaftBlock.POWERED))
			return defaultModifier;

		Direction direction = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());
		if (stateFrom.getValue(GantryCarriageBlock.FACING) != direction.getOpposite())
			return defaultModifier;
		return getGantryPinionModifier(stateTo.getValue(GantryShaftBlock.FACING), stateFrom.getValue(GantryCarriageBlock.FACING));
	}

	public static float getGantryPinionModifier(Direction shaft, Direction pinionDirection) {
		Axis shaftAxis = shaft.getAxis();
		float directionModifier = shaft.getAxisDirection()
			.getStep();
		if (shaftAxis == Axis.Y)
			if (pinionDirection == Direction.NORTH || pinionDirection == Direction.EAST)
				return -directionModifier;
		if (shaftAxis == Axis.X)
			if (pinionDirection == Direction.DOWN || pinionDirection == Direction.SOUTH)
				return -directionModifier;
		if (shaftAxis == Axis.Z)
			if (pinionDirection == Direction.UP || pinionDirection == Direction.WEST)
				return -directionModifier;
		return directionModifier;
	}

	private boolean shouldAssemble() {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof GantryCarriageBlock))
			return false;
		Direction facing = blockState.getValue(GantryCarriageBlock.FACING)
			.getOpposite();
		BlockState shaftState = level.getBlockState(worldPosition.relative(facing));
		if (!(shaftState.getBlock() instanceof GantryShaftBlock))
			return false;
		if (shaftState.getValue(GantryShaftBlock.POWERED))
			return false;
		BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
		return be instanceof GantryShaftBlockEntity && ((GantryShaftBlockEntity) be).canAssembleOn();
	}
}

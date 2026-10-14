package com.simibubi.create.content.trains.track;

import static com.simibubi.create.AllShapes.TRACK_ASC;
import static com.simibubi.create.AllShapes.TRACK_CROSS;
import static com.simibubi.create.AllShapes.TRACK_CROSS_DIAG;
import static com.simibubi.create.AllShapes.TRACK_CROSS_DIAG_ORTHO;
import static com.simibubi.create.AllShapes.TRACK_CROSS_ORTHO_DIAG;
import static com.simibubi.create.AllShapes.TRACK_DIAG;
import static com.simibubi.create.AllShapes.TRACK_ORTHO;
import static com.simibubi.create.AllShapes.TRACK_ORTHO_LONG;

import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrackBlock extends Block
	implements IBE<TrackBlockEntity>, IWrenchable, ITrackBlock, SpecialBlockItemRequirement, ProperWaterloggedBlock, IHaveBigOutline {

	public static final EnumProperty<TrackShape> SHAPE = EnumProperty.create("shape", TrackShape.class);
	public static final BooleanProperty HAS_BE = BooleanProperty.create("turn");

	protected final TrackMaterial material;

	public TrackBlock(Properties p_49795_, TrackMaterial material) {
		super(p_49795_);
		registerDefaultState(defaultBlockState().setValue(SHAPE, TrackShape.ZO)
			.setValue(HAS_BE, false)
			.setValue(WATERLOGGED, false));
		this.material = material;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> p_49915_) {
		super.createBlockStateDefinition(p_49915_.add(SHAPE, HAS_BE, WATERLOGGED));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return fluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState stateForPlacement = withWater(super.getStateForPlacement(ctx), ctx);

		if (ctx.getPlayer() == null)
			return stateForPlacement;

		Vec3 lookAngle = ctx.getPlayer()
			.getLookAngle();
		lookAngle = lookAngle.multiply(1, 0, 1);
		if (Mth.equal(lookAngle.length(), 0))
			lookAngle = VecHelper.rotate(new Vec3(0, 0, 1), -ctx.getPlayer()
				.getYRot(), Axis.Y);

		lookAngle = lookAngle.normalize();

		TrackShape best = TrackShape.ZO;
		double bestValue = Float.MAX_VALUE;
		for (TrackShape shape : TrackShape.values()) {
			if (shape.isJunction() || shape.isPortal())
				continue;
			Vec3 axis = shape.getAxes()
				.get(0);
			double distance = Math.min(axis.distanceToSqr(lookAngle), axis.normalize()
				.scale(-1)
				.distanceToSqr(lookAngle));
			if (distance > bestValue)
				continue;
			bestValue = distance;
			best = shape;
		}

		Level level = ctx.getLevel();
		Vec3 bestAxis = best.getAxes()
			.get(0);
		if (bestAxis.lengthSqr() == 1)
			for (boolean neg : Iterate.trueAndFalse) {
				BlockPos offset = ctx.getClickedPos()
					.offset(BlockPos.containing(bestAxis.scale(neg ? -1 : 1)));

				if (level.getBlockState(offset)
					.isFaceSturdy(level, offset, Direction.UP)
					&& !level.getBlockState(offset.above())
					.isFaceSturdy(level, offset, Direction.DOWN)) {
					if (best == TrackShape.XO)
						best = neg ? TrackShape.AW : TrackShape.AE;
					if (best == TrackShape.ZO)
						best = neg ? TrackShape.AN : TrackShape.AS;
				}
			}

		return stateForPlacement.setValue(SHAPE, best);
	}

	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction pDirection, BlockState pNeighborState,
								  LevelAccessor level, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		updateWater(level, state, pCurrentPos);
		return state;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return getFullShape(state);
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter pLevel, BlockPos pPos) {
		return getFullShape(state);
	}

	private VoxelShape getFullShape(BlockState state) {
		switch (state.getValue(SHAPE)) {
			case AE:
				return TRACK_ASC.get(Direction.EAST);
			case AW:
				return TRACK_ASC.get(Direction.WEST);
			case AN:
				return TRACK_ASC.get(Direction.NORTH);
			case AS:
				return TRACK_ASC.get(Direction.SOUTH);
			case CR_D:
				return TRACK_CROSS_DIAG;
			case CR_NDX:
				return TRACK_CROSS_ORTHO_DIAG.get(Direction.SOUTH);
			case CR_NDZ:
				return TRACK_CROSS_DIAG_ORTHO.get(Direction.SOUTH);
			case CR_O:
				return TRACK_CROSS;
			case CR_PDX:
				return TRACK_CROSS_DIAG_ORTHO.get(Direction.EAST);
			case CR_PDZ:
				return TRACK_CROSS_ORTHO_DIAG.get(Direction.EAST);
			case ND:
				return TRACK_DIAG.get(Direction.SOUTH);
			case PD:
				return TRACK_DIAG.get(Direction.EAST);
			case XO:
				return TRACK_ORTHO.get(Direction.EAST);
			case ZO:
				return TRACK_ORTHO.get(Direction.SOUTH);
			case TE:
				return TRACK_ORTHO_LONG.get(Direction.EAST);
			case TW:
				return TRACK_ORTHO_LONG.get(Direction.WEST);
			case TS:
				return TRACK_ORTHO_LONG.get(Direction.SOUTH);
			case TN:
				return TRACK_ORTHO_LONG.get(Direction.NORTH);
			case NONE:
			default:
		}
		return AllShapes.TRACK_FALLBACK;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos,
										CollisionContext pContext) {
		switch (pState.getValue(SHAPE)) {
			case AE, AW, AN, AS:
				return Shapes.empty();
			default:
				return AllShapes.TRACK_COLLISION;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState state) {
		if (!state.getValue(HAS_BE))
			return null;
		return AllBlockEntityTypes.TRACK.create(p_153215_, state);
	}

	@Override
	public Class<TrackBlockEntity> getBlockEntityClass() {
		return TrackBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends TrackBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.TRACK.get();
	}

	@Override
	public Vec3 getUpNormal(BlockGetter world, BlockPos pos, BlockState state) {
		return state.getValue(SHAPE)
			.getNormal();
	}

	@Override
	public List<Vec3> getTrackAxes(BlockGetter world, BlockPos pos, BlockState state) {
		return state.getValue(SHAPE)
			.getAxes();
	}

	@Override
	public Vec3 getCurveStart(BlockGetter world, BlockPos pos, BlockState state, Vec3 axis) {
		boolean vertical = axis.y != 0;
		return VecHelper.getCenterOf(pos)
			.add(0, (vertical ? 0 : -.5f), 0)
			.add(axis.scale(.5));
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState overlay(BlockGetter world, BlockPos pos, BlockState existing, BlockState placed) {
		if (placed.getBlock() != this)
			return existing;

		TrackShape existingShape = existing.getValue(SHAPE);
		TrackShape placedShape = placed.getValue(SHAPE);
		TrackShape combinedShape = null;

		for (boolean flip : Iterate.trueAndFalse) {
			TrackShape s1 = flip ? existingShape : placedShape;
			TrackShape s2 = flip ? placedShape : existingShape;
			if (s1 == TrackShape.XO && s2 == TrackShape.ZO)
				combinedShape = TrackShape.CR_O;
			if (s1 == TrackShape.PD && s2 == TrackShape.ND)
				combinedShape = TrackShape.CR_D;
			if (s1 == TrackShape.XO && s2 == TrackShape.PD)
				combinedShape = TrackShape.CR_PDX;
			if (s1 == TrackShape.ZO && s2 == TrackShape.PD)
				combinedShape = TrackShape.CR_PDZ;
			if (s1 == TrackShape.XO && s2 == TrackShape.ND)
				combinedShape = TrackShape.CR_NDX;
			if (s1 == TrackShape.ZO && s2 == TrackShape.ND)
				combinedShape = TrackShape.CR_NDZ;
		}

		if (combinedShape != null)
			existing = existing.setValue(SHAPE, combinedShape);
		return existing;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation pRotation) {
		return state.setValue(SHAPE, state.getValue(SHAPE)
			.rotate(pRotation));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror pMirror) {
		return state.setValue(SHAPE, state.getValue(SHAPE)
			.mirror(pMirror));
	}

	@Override
	public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
		return AllBlocks.SMALL_BOGEY.getDefaultState()
			.setValue(BlockStateProperties.HORIZONTAL_AXIS, state.getValue(SHAPE) == TrackShape.XO ? Axis.X : Axis.Z);
	}

	@Override
	public boolean trackEquals(BlockState state1, BlockState state2) {
		return state1.getBlock() == state2.getBlock() && state1.getValue(SHAPE) == state2.getValue(SHAPE);
	}

	@Override
	public ItemRequirement getRequiredItems(BlockState state, BlockEntity te) {
		return new ItemRequirement(ItemUseType.CONSUME, new ItemStack(state.getBlock()));
	}

	@Override
	public TrackMaterial getMaterial() {
		return material;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}
}

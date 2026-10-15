package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FactoryPanelBlock extends FaceAttachedHorizontalDirectionalBlock
	implements ProperWaterloggedBlock, IBE<FactoryPanelBlockEntity>, IWrenchable {
	public static final MapCodec<FactoryPanelBlock> CODEC = simpleCodec(FactoryPanelBlock::new);

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public enum PanelSlot implements net.minecraft.util.StringRepresentable {
		TOP_LEFT(1, 1),
		TOP_RIGHT(0, 1),
		BOTTOM_LEFT(1, 0),
		BOTTOM_RIGHT(0, 0);

		public static final com.mojang.serialization.Codec<PanelSlot> CODEC = net.minecraft.util.StringRepresentable.fromValues(PanelSlot::values);
		public static final net.minecraft.network.codec.StreamCodec<io.netty.buffer.ByteBuf, PanelSlot> STREAM_CODEC =
			net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders.ofEnum(PanelSlot.class);

		public final int xOffset;
		public final int yOffset;

		PanelSlot(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		@Override
		public @org.jetbrains.annotations.NotNull String getSerializedName() {
			return net.createmod.catnip.lang.Lang.asId(name());
		}
	}

	public static enum PanelState {
		PASSIVE, ACTIVE
	}

	public static enum PanelType {
		NETWORK, PACKAGER
	}

	public FactoryPanelBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACE, FACING, WATERLOGGED, POWERED));
	}

	public static Direction connectedDirection(BlockState state) {
		return getConnectedDirection(state);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState stateForPlacement = super.getStateForPlacement(pContext);
		if (stateForPlacement == null)
			return null;
		if (stateForPlacement.getValue(FACE) == AttachFace.FLOOR)
			stateForPlacement = stateForPlacement.setValue(FACING, stateForPlacement.getValue(FACING).getOpposite());
		return withWater(stateForPlacement, pContext);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return fluidState(pState);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
		LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		updateWater(pLevel, pState, pPos);
		return pState;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return AllShapes.FACTORY_PANEL_FALLBACK.get(getConnectedDirection(pState));
	}

	@Override
	public Class<FactoryPanelBlockEntity> getBlockEntityClass() {
		return FactoryPanelBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends FactoryPanelBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.FACTORY_PANEL.get();
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public void onRemove(BlockState pState, net.minecraft.world.level.Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
	}

	public static PanelSlot getTargetedSlot(BlockPos pos, BlockState blockState, net.minecraft.world.phys.Vec3 clickLocation) {
		double bestDistance = Double.MAX_VALUE;
		PanelSlot bestSlot = PanelSlot.BOTTOM_LEFT;
		net.minecraft.world.phys.Vec3 localClick = clickLocation.subtract(net.minecraft.world.phys.Vec3.atLowerCornerOf(pos));
		float xRot = net.minecraft.util.Mth.RAD_TO_DEG * FactoryPanelBlock.getXRot(blockState);
		float yRot = net.minecraft.util.Mth.RAD_TO_DEG * FactoryPanelBlock.getYRot(blockState);

		for (PanelSlot slot : PanelSlot.values()) {
			net.minecraft.world.phys.Vec3 vec = new net.minecraft.world.phys.Vec3(.25 + slot.xOffset * .5, 0, .25 + slot.yOffset * .5);
			vec = net.createmod.catnip.math.VecHelper.rotateCentered(vec, 180, Direction.Axis.Y);
			vec = net.createmod.catnip.math.VecHelper.rotateCentered(vec, xRot + 90, Direction.Axis.X);
			vec = net.createmod.catnip.math.VecHelper.rotateCentered(vec, yRot, Direction.Axis.Y);

			double diff = vec.distanceToSqr(localClick);
			if (diff > bestDistance)
				continue;
			bestDistance = diff;
			bestSlot = slot;
		}

		return bestSlot;
	}

	public static float getXRot(BlockState state) {
		AttachFace face = state.getOptionalValue(FactoryPanelBlock.FACE).orElse(AttachFace.FLOOR);
		return face == AttachFace.CEILING ? net.minecraft.util.Mth.PI / 2 : face == AttachFace.FLOOR ? -net.minecraft.util.Mth.PI / 2 : 0;
	}

	public static float getYRot(BlockState state) {
		Direction facing = state.getOptionalValue(FactoryPanelBlock.FACING).orElse(Direction.SOUTH);
		AttachFace face = state.getOptionalValue(FactoryPanelBlock.FACE).orElse(AttachFace.FLOOR);
		return (face == AttachFace.CEILING ? net.minecraft.util.Mth.PI : 0) + net.createmod.catnip.math.AngleHelper.rad(net.createmod.catnip.math.AngleHelper.horizontalAngle(facing));
	}

	@Override
	protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
		return CODEC;
	}

}

package com.simibubi.create.content.kinetics.waterwheel;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

public class WaterWheelStructuralBlock extends DirectionalBlock implements IWrenchable {

	public static final MapCodec<WaterWheelStructuralBlock> CODEC = simpleCodec(WaterWheelStructuralBlock::new);

	public WaterWheelStructuralBlock(Properties p_52591_) {
		super(p_52591_);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(FACING));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.INVISIBLE;
	}

	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.BLOCK;
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.PASS;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return AllBlocks.LARGE_WATER_WHEEL.asStack();
	}

	@Override
	public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		BlockPos clickedPos = context.getClickedPos();
		Level level = context.getLevel();

		if (stillValid(level, clickedPos, state, false)) {
			BlockPos masterPos = getMaster(level, clickedPos, state);
			context = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(),
				new BlockHitResult(context.getClickLocation(), context.getClickedFace(), masterPos,
					context.isInside()));
			state = level.getBlockState(masterPos);
		}

		return IWrenchable.super.onSneakWrenched(state, context);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!stillValid(level, pos, state, false))
			return ItemInteractionResult.FAIL;
		if (!(level.getBlockEntity(getMaster(level, pos, state)) instanceof WaterWheelBlockEntity wwt))
			return ItemInteractionResult.FAIL;
		return wwt.applyMaterialIfValid(stack);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (stillValid(pLevel, pPos, pState, false))
			pLevel.destroyBlock(getMaster(pLevel, pPos, pState), true);
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	@Override
	public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (stillValid(pLevel, pPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pPos, pState);
			pLevel.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
			if (!pLevel.isClientSide() && pPlayer.isCreative())
				pLevel.destroyBlock(masterPos, false);
		}
		return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
								  BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (stillValid(pLevel, pCurrentPos, pState, false)) {
			BlockPos masterPos = getMaster(pLevel, pCurrentPos, pState);
			if (!pLevel.getBlockTicks()
				.hasScheduledTick(masterPos, AllBlocks.LARGE_WATER_WHEEL.get()))
				pLevel.scheduleTick(masterPos, AllBlocks.LARGE_WATER_WHEEL.get(), 1);
			return pState;
		}
		if (!(pLevel instanceof Level level) || level.isClientSide())
			return pState;
		if (!level.getBlockTicks()
			.hasScheduledTick(pCurrentPos, this))
			level.scheduleTick(pCurrentPos, this, 1);
		return pState;
	}

	public static BlockPos getMaster(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);
		if (targetedState.is(AllBlocks.WATER_WHEEL_STRUCTURAL.get()))
			return getMaster(level, targetedPos, targetedState);
		return targetedPos;
	}

	public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state, boolean directlyAdjacent) {
		if (!state.is(this))
			return false;

		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);

		if (!directlyAdjacent && stillValid(level, targetedPos, targetedState, true))
			return true;
		return targetedState.getBlock() instanceof LargeWaterWheelBlock
			&& targetedState.getValue(LargeWaterWheelBlock.AXIS) != direction.getAxis();
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!stillValid(pLevel, pPos, pState, false))
			pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}
}

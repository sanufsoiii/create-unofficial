package com.simibubi.create.content.logistics.redstoneRequester;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public class RedstoneRequesterBlock extends Block implements IBE<RedstoneRequesterBlockEntity>, IWrenchable {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public RedstoneRequesterBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(defaultBlockState().setValue(POWERED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder.add(POWERED, AXIS));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(AXIS, pContext.getHorizontalDirection().getClockWise().getAxis())
			.setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
		boolean pIsMoving) {
		super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
		if (pLevel.isClientSide())
			return;
		boolean isPowered = pLevel.hasNeighborSignal(pPos);
		if (isPowered != pState.getValue(POWERED)) {
			pLevel.setBlock(pPos, pState.setValue(POWERED, isPowered), 2);
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.getItem() instanceof LogisticallyLinkedBlockItem)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public Class<RedstoneRequesterBlockEntity> getBlockEntityClass() {
		return RedstoneRequesterBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends RedstoneRequesterBlockEntity> getBlockEntityType() {
		return AllBlockEntityTypes.REDSTONE_REQUESTER.get();
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

}

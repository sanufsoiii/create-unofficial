package com.simibubi.create.content.logistics.chute;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.AdventureUtil;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractChuteBlock extends Block implements IWrenchable, IBE<ChuteBlockEntity> {

	public AbstractChuteBlock(Properties properties) {
		super(properties);
	}

	public static boolean isChute(BlockState state) {
		return state.getBlock() instanceof AbstractChuteBlock;
	}

	public static boolean isOpenChute(BlockState state) {
		return isChute(state) && ((AbstractChuteBlock) state.getBlock()).isOpen(state);
	}

	public static boolean isTransparentChute(BlockState state) {
		return isChute(state) && ((AbstractChuteBlock) state.getBlock()).isTransparent(state);
	}

	@Nullable
	public static Direction getChuteFacing(BlockState state) {
		return !isChute(state) ? null : ((AbstractChuteBlock) state.getBlock()).getFacing(state);
	}

	public Direction getFacing(BlockState state) {
		return Direction.DOWN;
	}

	public boolean isOpen(BlockState state) {
		return true;
	}

	public boolean isTransparent(BlockState state) {
		return false;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		AdvancementBehaviour.setPlacedBy(level, pos, placer);
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
		if (!(entityIn instanceof ItemEntity))
			return;
		if (entityIn.level().isClientSide)
			return;
		if (!entityIn.isAlive())
			return;
		DirectBeltInputBehaviour input = BlockEntityBehaviour.get(entityIn.level(),
			BlockPos.containing(entityIn.position().add(0, 0.5f, 0)).below(),
			DirectBeltInputBehaviour.TYPE);
		if (input == null)
			return;
		if (!input.canInsertFromSide(Direction.UP))
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		ItemStack toInsert = itemEntity.getItem();
		ItemStack remainder = input.handleInsertion(toInsert, Direction.UP, false);

		if (remainder.isEmpty())
			itemEntity.discard();
		if (remainder.getCount() < toInsert.getCount())
			itemEntity.setItem(remainder);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		withBlockEntityDo(world, pos, ChuteBlockEntity::onAdded);
		updateDiagonalNeighbour(state, world, pos);
	}

	protected void updateDiagonalNeighbour(BlockState state, Level world, BlockPos pos) {
		if (!isChute(state))
			return;
		AbstractChuteBlock block = (AbstractChuteBlock) state.getBlock();
		Direction facing = block.getFacing(state);
		BlockPos toUpdate = pos.below();
		if (facing.getAxis().isHorizontal())
			toUpdate = toUpdate.relative(facing.getOpposite());

		BlockState stateToUpdate = world.getBlockState(toUpdate);
		if (isChute(stateToUpdate) && !world.getBlockTicks().hasScheduledTick(toUpdate, stateToUpdate.getBlock()))
			world.scheduleTick(toUpdate, stateToUpdate.getBlock(), 1);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, world, pos, newState);

		if (state.is(newState.getBlock()))
			return;

		updateDiagonalNeighbour(state, world, pos);

		for (Direction direction : Iterate.horizontalDirections) {
			BlockPos toUpdate = pos.above().relative(direction);
			BlockState stateToUpdate = world.getBlockState(toUpdate);
			if (isChute(stateToUpdate) && !world.getBlockTicks().hasScheduledTick(toUpdate, stateToUpdate.getBlock()))
				world.scheduleTick(toUpdate, stateToUpdate.getBlock(), 1);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockState updated = updateChuteState(state, level.getBlockState(pos.above()), level, pos);
		if (state != updated)
			level.setBlockAndUpdate(pos, updated);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState above, LevelAccessor world,
		BlockPos pos, BlockPos neighborPos) {
		if (direction != Direction.UP)
			return state;
		return updateChuteState(state, above, world, pos);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block,
		BlockPos neighbourPos, boolean isMoving) {
		if (pos.below().equals(neighbourPos))
			withBlockEntityDo(world, pos, ChuteBlockEntity::blockBelowChanged);
	}

	public abstract BlockState updateChuteState(BlockState state, BlockState above, BlockGetter world, BlockPos pos);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return ChuteShapes.getShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return ChuteShapes.getCollisionShape(state);
	}

	@Override
	public Class<ChuteBlockEntity> getBlockEntityClass() {
		return ChuteBlockEntity.class;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		if (AdventureUtil.isAdventure(player))
			return InteractionResult.PASS;
		if (world.isClientSide)
			return InteractionResult.SUCCESS;

		return onBlockEntityUse(world, pos, be -> {
			if (be.item.isEmpty())
				return InteractionResult.PASS;
			player.getInventory().placeItemBackInInventory(be.item);
			be.setItem(ItemStack.EMPTY);
			return InteractionResult.SUCCESS;
		});
	}

}

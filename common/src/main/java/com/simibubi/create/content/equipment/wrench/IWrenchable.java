package com.simibubi.create.content.equipment.wrench;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface IWrenchable {

	BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.create("axis_along_first");

	default InteractionResult onWrenched(BlockState state, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState rotated = getRotatedBlockState(state, context.getClickedFace());
		if (!rotated.canSurvive(level, context.getClickedPos()))
			return InteractionResult.PASS;

		WrenchHelper.switchToBlockState(level, pos, updateAfterWrenched(rotated, context));

		if (level.getBlockState(pos) != state)
			playRotateSound(level, pos);

		return InteractionResult.SUCCESS;
	}

	default BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
//		return newState;
		return Block.updateFromNeighbourShapes(newState, context.getLevel(), context.getClickedPos());
	}

	default InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();

		if (!(world instanceof ServerLevel serverLevel))
			return InteractionResult.SUCCESS;

		if (WrenchHelper.postSneakWrenchBreakEvent(world, pos, state, player))
			return InteractionResult.SUCCESS;

		if (player != null && !player.isCreative()) {
			Block.getDrops(state, serverLevel, pos, world.getBlockEntity(pos), player, context.getItemInHand())
				.forEach(itemStack -> {
					player.getInventory()
						.placeItemBackInInventory(itemStack);
				});
		}

		state.spawnAfterBreak(serverLevel, pos, ItemStack.EMPTY, true);
		world.destroyBlock(pos, false);
		playRemoveSound(world, pos);
		return InteractionResult.SUCCESS;
	}

	static void playRemoveSound(Level level, BlockPos pos) {
		AllSoundEvents.WRENCH_REMOVE.playOnServer(level, pos, 1, Create.RANDOM.nextFloat() * .5f + .5f);
	}

	static void playRotateSound(Level level, BlockPos pos) {
		AllSoundEvents.WRENCH_ROTATE.playOnServer(level, pos, 1, Create.RANDOM.nextFloat() + .5f);
	}

	default BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		BlockState newState = originalState;

		if (targetedFace.getAxis() == Direction.Axis.Y) {
			if (originalState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
				return originalState.setValue(BlockStateProperties.HORIZONTAL_AXIS, VoxelShaper
					.axisAsFace(originalState.getValue(BlockStateProperties.HORIZONTAL_AXIS))
					.getClockWise(targetedFace.getAxis())
					.getAxis());
			if (originalState.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
				return originalState.setValue(BlockStateProperties.HORIZONTAL_FACING, originalState
					.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise(targetedFace.getAxis()));
		}

		if (originalState.hasProperty(BlockStateProperties.AXIS))
			return originalState.setValue(BlockStateProperties.AXIS,
				VoxelShaper
					.axisAsFace(originalState.getValue(BlockStateProperties.AXIS))
					.getClockWise(targetedFace.getAxis())
					.getAxis());

		if (!originalState.hasProperty(BlockStateProperties.FACING))
			return originalState;

		Direction stateFacing = originalState.getValue(BlockStateProperties.FACING);

		if (stateFacing.getAxis()
			.equals(targetedFace.getAxis())) {
			if (originalState.hasProperty(AXIS_ALONG_FIRST_COORDINATE))
				return originalState.cycle(AXIS_ALONG_FIRST_COORDINATE);
			else
				return originalState;
		} else {
			do {
				newState = newState.setValue(BlockStateProperties.FACING,
					newState.getValue(BlockStateProperties.FACING).getClockWise(targetedFace.getAxis()));
				if (targetedFace.getAxis() == Direction.Axis.Y
					&& newState.hasProperty(AXIS_ALONG_FIRST_COORDINATE))
					newState = newState.cycle(AXIS_ALONG_FIRST_COORDINATE);
			} while (newState.getValue(BlockStateProperties.FACING)
				.getAxis()
				.equals(targetedFace.getAxis()));
		}
		return newState;
	}
}

package com.simibubi.create.foundation.utility;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Pure vanilla block helper methods.
 * Platform-independent and safe to use in common.
 */
public class CommonBlockHelper {

	private static final List<IntegerProperty> COUNT_STATES = List.of(
		BlockStateProperties.EGGS,
		BlockStateProperties.PICKLES,
		BlockStateProperties.CANDLES
	);

	private static final List<Block> VINELIKE_BLOCKS = List.of(
		Blocks.VINE, Blocks.GLOW_LICHEN
	);

	private static final List<BooleanProperty> VINELIKE_STATES = List.of(
		BlockStateProperties.UP,
		BlockStateProperties.NORTH,
		BlockStateProperties.EAST,
		BlockStateProperties.SOUTH,
		BlockStateProperties.WEST,
		BlockStateProperties.DOWN
	);

	public static BlockState setZeroAge(BlockState blockState) {
		if (blockState.hasProperty(BlockStateProperties.AGE_1))
			return blockState.setValue(BlockStateProperties.AGE_1, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_2))
			return blockState.setValue(BlockStateProperties.AGE_2, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_3))
			return blockState.setValue(BlockStateProperties.AGE_3, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_5))
			return blockState.setValue(BlockStateProperties.AGE_5, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_7))
			return blockState.setValue(BlockStateProperties.AGE_7, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_15))
			return blockState.setValue(BlockStateProperties.AGE_15, 0);
		if (blockState.hasProperty(BlockStateProperties.AGE_25))
			return blockState.setValue(BlockStateProperties.AGE_25, 0);
		if (blockState.hasProperty(BlockStateProperties.LEVEL_HONEY))
			return blockState.setValue(BlockStateProperties.LEVEL_HONEY, 0);
		if (blockState.hasProperty(BlockStateProperties.HATCH))
			return blockState.setValue(BlockStateProperties.HATCH, 0);
		if (blockState.hasProperty(BlockStateProperties.STAGE))
			return blockState.setValue(BlockStateProperties.STAGE, 0);
		if (blockState.is(BlockTags.CAULDRONS))
			return Blocks.CAULDRON.defaultBlockState();
		if (blockState.hasProperty(BlockStateProperties.LEVEL_COMPOSTER))
			return blockState.setValue(BlockStateProperties.LEVEL_COMPOSTER, 0);
		if (blockState.hasProperty(BlockStateProperties.EXTENDED))
			return blockState.setValue(BlockStateProperties.EXTENDED, false);
		return blockState;
	}

	public static int findAndRemoveInInventory(BlockState block, Player player, int amount) {
		int amountFound = 0;
		Item required = getRequiredItem(block).getItem();

		boolean needsTwo = block.hasProperty(BlockStateProperties.SLAB_TYPE)
			&& block.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE;

		if (needsTwo)
			amount *= 2;

		for (IntegerProperty property : COUNT_STATES)
			if (block.hasProperty(property))
				amount *= block.getValue(property);

		if (VINELIKE_BLOCKS.contains(block.getBlock())) {
			int vineCount = 0;

			for (BooleanProperty vineState : VINELIKE_STATES) {
				if (block.hasProperty(vineState) && block.getValue(vineState)) {
					vineCount++;
				}
			}

			amount += vineCount - 1;
		}

		{
			// Try held Item first
			int preferredSlot = player.getInventory().selected;
			ItemStack itemstack = player.getInventory()
				.getItem(preferredSlot);
			int count = itemstack.getCount();
			if (itemstack.getItem() == required && count > 0) {
				int taken = Math.min(count, amount - amountFound);
				player.getInventory()
					.setItem(preferredSlot, new ItemStack(itemstack.getItem(), count - taken));
				amountFound += taken;
			}
		}

		// Search inventory
		for (int i = 0; i < player.getInventory()
			.getContainerSize(); ++i) {
			if (amountFound == amount)
				break;

			ItemStack itemstack = player.getInventory()
				.getItem(i);
			int count = itemstack.getCount();
			if (itemstack.getItem() == required && count > 0) {
				int taken = Math.min(count, amount - amountFound);
				player.getInventory()
					.setItem(i, new ItemStack(itemstack.getItem(), count - taken));
				amountFound += taken;
			}
		}

		if (needsTwo) {
			// Give back 1 if uneven amount was removed
			if (amountFound % 2 != 0)
				player.getInventory()
					.add(new ItemStack(required));
			amountFound /= 2;
		}

		return amountFound;
	}

	public static ItemStack getRequiredItem(BlockState state) {
		ItemStack itemStack = new ItemStack(state.getBlock());
		Item item = itemStack.getItem();
		if (item == Items.FARMLAND || item == Items.DIRT_PATH)
			itemStack = new ItemStack(Items.DIRT);
		return itemStack;
	}

	public static boolean isSolidWall(BlockGetter reader, BlockPos fromPos, Direction toDirection) {
		return hasBlockSolidSide(reader.getBlockState(fromPos.relative(toDirection)), reader,
			fromPos.relative(toDirection), toDirection.getOpposite());
	}

	public static boolean noCollisionInSpace(BlockGetter reader, BlockPos pos) {
		return reader.getBlockState(pos)
			.getCollisionShape(reader, pos)
			.isEmpty();
	}

	public static double getBounceMultiplier(Block block) {
		if (block instanceof SlimeBlock)
			return 0.8D;
		if (block instanceof BedBlock)
			return 0.66 * 0.8D;
		return 0;
	}

	public static boolean hasBlockSolidSide(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction dir) {
		return !state.is(BlockTags.LEAVES)
			&& Block.isFaceFull(state.getCollisionShape(blockGetter, pos), dir);
	}

	public static boolean extinguishFire(Level world, @Nullable Player player, BlockPos pos, Direction dir) {
		pos = pos.relative(dir);
		if (world.getBlockState(pos)
			.getBlock() == Blocks.FIRE) {
			world.levelEvent(player, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
			world.removeBlock(pos, false);
			return true;
		} else {
			return false;
		}
	}

	public static BlockState copyProperties(BlockState fromState, BlockState toState) {
		for (Property<?> property : fromState.getProperties()) {
			toState = copyProperty(property, fromState, toState);
		}
		return toState;
	}

	public static <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState fromState,
																	BlockState toState) {
		if (fromState.hasProperty(property) && toState.hasProperty(property)) {
			return toState.setValue(property, fromState.getValue(property));
		}
		return toState;
	}

	public static InteractionResult invokeUse(BlockState state, Level level, Player player,
											   InteractionHand hand, BlockHitResult ray) {
		ItemInteractionResult iteminteractionresult = state.useItemOn(
				player.getItemInHand(hand), level, player, hand, ray
		);
		if (iteminteractionresult.consumesAction()) {
			return iteminteractionresult.result();
		}

		if (iteminteractionresult == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && hand == InteractionHand.MAIN_HAND) {
			InteractionResult interactionresult = state.useWithoutItem(level, player, ray);
			if (interactionresult.consumesAction()) {
				return interactionresult;
			}
		}

		return InteractionResult.PASS;
	}

	@ExpectPlatform
	public static BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
		@Nullable BlockState querySource, @Nullable BlockPos queryPos) {
		throw new AssertionError("@ExpectPlatform");
	}
}

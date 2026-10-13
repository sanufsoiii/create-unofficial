package com.simibubi.create.foundation.utility;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CampfireBlock;

public class BlockHelper extends CommonBlockHelper {

	public static boolean isNotUnheated(BlockState state) {
		if (state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT)) {
			return state.getValue(CampfireBlock.LIT);
		}
		if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
			return state.getValue(BlazeBurnerBlock.HEAT_LEVEL) != HeatLevel.NONE;
		}
		return true;
	}

	public static void destroyBlock(Level world, BlockPos pos, float effectChance) {
		destroyBlock(world, pos, effectChance, stack -> Block.popResource(world, pos, stack));
	}

	public static void destroyBlock(Level world, BlockPos pos, float effectChance,
			Consumer<ItemStack> droppedItemCallback) {
		destroyBlockAs(world, pos, null, ItemStack.EMPTY, effectChance, droppedItemCallback);
	}

	public static void destroyBlockAs(Level world, BlockPos pos, @Nullable Player player, ItemStack usedTool,
			float effectChance, Consumer<ItemStack> droppedItemCallback) {
		FluidState fluidState = world.getFluidState(pos);
		BlockState state = world.getBlockState(pos);

		if (world.random.nextFloat() < effectChance)
			world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

		if (player != null) {
			boolean allowed = PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, state,
					blockEntity);
			if (!allowed) {
				PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(world, player, pos, state, blockEntity);
				return;
			}

			usedTool.mineBlock(world, state, pos, player);
			player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
		}

		if (world instanceof ServerLevel && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
				&& (player == null || !player.isCreative())) {
			for (ItemStack itemStack : Block.getDrops(state, (ServerLevel) world, pos, blockEntity, player, usedTool)) {
				if (itemStack.isEmpty())
					continue;
				droppedItemCallback.accept(itemStack);
			}

			if (state.getBlock() instanceof IceBlock
					&& EnchantmentHelper.getItemEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), usedTool) == 0) {
				if (world.dimensionType().ultraWarm())
					return;

				BlockState blockstate = world.getBlockState(pos.below());
				if (blockstate.blocksMotion() || blockstate.liquid()) {
					world.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
					afterBreak(world, player, pos, state, blockEntity);
				}
				return;
			}

			state.spawnAfterBreak((ServerLevel) world, pos, ItemStack.EMPTY, true);
		}

		world.setBlockAndUpdate(pos, fluidState.createLegacyBlock());
		afterBreak(world, player, pos, state, blockEntity);
	}

	private static void afterBreak(Level level, @Nullable Player player, BlockPos pos, BlockState state,
			@Nullable BlockEntity be) {
		if (player != null)
			PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(level, player, pos, state, be);
	}
}

package com.simibubi.create.foundation.utility;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.sounds.SoundEvents;
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

	public static CompoundTag prepareBlockEntityData(Level level, BlockState blockState, BlockEntity blockEntity) {
		CompoundTag data = null;
		if (blockEntity == null)
			return null;
		net.minecraft.core.RegistryAccess access = level.registryAccess();
		com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry.SafeNbtWriter writer = com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry.REGISTRY.get(blockEntity.getType());
		if (com.simibubi.create.AllTags.AllBlockTags.SAFE_NBT.matches(blockState)) {
			data = blockEntity.saveWithFullMetadata(access);
		} else if (writer != null) {
			data = new CompoundTag();
			writer.writeSafe(blockEntity, data, access);
		} else if (blockEntity instanceof com.simibubi.create.api.schematic.nbt.PartialSafeNBT safeNbtBE) {
			data = new CompoundTag();
			safeNbtBE.writeSafe(data, access);
		}

		return net.createmod.catnip.nbt.NBTProcessors.process(blockState, blockEntity, data, true);
	}

	public static void placeSchematicBlock(Level world, BlockState state, BlockPos target, ItemStack stack,
										   @Nullable CompoundTag data) {
		Block block = state.getBlock();
		BlockEntity existingBlockEntity = world.getBlockEntity(target);
		boolean alreadyPlaced = false;

		com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry.StateFilter filter = com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry.REGISTRY.get(state);
		if (filter != null) {
			state = filter.filterStates(existingBlockEntity, state);
		} else if (block instanceof com.simibubi.create.api.schematic.state.SchematicStateFilter schematicStateFilter) {
			state = schematicStateFilter.filterStates(existingBlockEntity, state);
		}

		// Piston
		if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.EXTENDED))
			state = state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.EXTENDED, Boolean.FALSE);
		if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED))
			state = state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED, Boolean.FALSE);

		if (block == Blocks.COMPOSTER) {
			state = Blocks.COMPOSTER.defaultBlockState();
		} else if (state.is(BlockTags.CAULDRONS)) {
			state = Blocks.CAULDRON.defaultBlockState();
		}

		if (world.dimensionType().ultraWarm() && state.getFluidState().is(net.minecraft.tags.FluidTags.WATER)) {
			int i = target.getX();
			int j = target.getY();
			int k = target.getZ();
			world.playSound(null, target, SoundEvents.FIRE_EXTINGUISH, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F,
				2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

			for (int l = 0; l < 8; ++l) {
				world.addParticle(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(),
					0.0D, 0.0D, 0.0D);
			}
			Block.dropResources(state, world, target);
			return;
		}

		if (alreadyPlaced) {
			// pass
		} else if (state.getBlock() instanceof net.minecraft.world.level.block.BaseRailBlock) {
			placeRailWithoutUpdate(world, state, target);
		} else if (com.simibubi.create.AllBlocks.BELT.has(state)) {
			world.setBlock(target, state, Block.UPDATE_CLIENTS);
		} else {
			world.setBlock(target, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		}

		if (data != null) {
			if (existingBlockEntity instanceof com.simibubi.create.foundation.blockEntity.IMergeableBE mergeable) {
				BlockEntity loaded = BlockEntity.loadStatic(target, state, data, world.registryAccess());
				if (loaded != null) {
					if (existingBlockEntity.getType().equals(loaded.getType())) {
						mergeable.accept(loaded);
						return;
					}
				}
			}
			BlockEntity blockEntity = world.getBlockEntity(target);
			if (blockEntity != null) {
				data.putInt("x", target.getX());
				data.putInt("y", target.getY());
				data.putInt("z", target.getZ());
				if (blockEntity instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity kbe)
					kbe.warnOfMovement();
				if (blockEntity instanceof com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer imbe)
					if (!imbe.isController())
						data.put("Controller", net.minecraft.nbt.NbtUtils.writeBlockPos(imbe.getController()));
				blockEntity.loadWithComponents(data, world.registryAccess());
			}
		}

		try {
			state.getBlock().setPlacedBy(world, target, state, null, stack);
		} catch (Exception ignored) {
		}
	}

	private static void placeRailWithoutUpdate(Level world, BlockState state, BlockPos target) {
		net.minecraft.world.level.chunk.LevelChunk chunk = world.getChunkAt(target);
		int idx = chunk.getSectionIndex(target.getY());
		net.minecraft.world.level.chunk.LevelChunkSection chunksection = chunk.getSection(idx);
		if (chunksection == null) {
			chunksection = new net.minecraft.world.level.chunk.LevelChunkSection(world.registryAccess()
				.registryOrThrow(Registries.BIOME));
			chunk.getSections()[idx] = chunksection;
		}
		BlockState old = chunksection.setBlockState(net.minecraft.core.SectionPos.sectionRelative(target.getX()),
			net.minecraft.core.SectionPos.sectionRelative(target.getY()), net.minecraft.core.SectionPos.sectionRelative(target.getZ()), state);
		chunk.setUnsaved(true);
		world.markAndNotifyBlock(target, chunk, old, state, 82, 512);

		world.setBlock(target, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_MOVE_BY_PISTON);
		world.neighborChanged(target, world.getBlockState(target.below()).getBlock(), target.below());
	}
}


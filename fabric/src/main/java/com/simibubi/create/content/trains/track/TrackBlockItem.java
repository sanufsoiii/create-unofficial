package com.simibubi.create.content.trains.track;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class TrackBlockItem extends BlockItem {

	public TrackBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (player.isShiftKeyDown() && isFoil(stack)) {
			return clearSelection(stack, level, player);
		}
		return super.use(level, player, usedHand);
	}

	public static InteractionResultHolder<ItemStack> clearSelection(ItemStack stack, Level level, Player player) {
		if (level.isClientSide) {
			level.playSound(player, player.blockPosition(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
		} else {
			player.displayClientMessage(CreateLang.translateDirect("track.selection_cleared"), true);
			stack.remove(AllDataComponents.TRACK_CONNECTING_FROM);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(AllDataComponents.TRACK_CONNECTING_FROM);
	}
}

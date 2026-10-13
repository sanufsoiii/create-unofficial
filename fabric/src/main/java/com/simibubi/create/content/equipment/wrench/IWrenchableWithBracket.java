package com.simibubi.create.content.equipment.wrench;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IWrenchableWithBracket extends IWrenchable {

	Optional<ItemStack> removeBracket(BlockGetter world, BlockPos pos, boolean inOnReplacedContext);

	@Override
	default InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (tryRemoveBracket(context))
			return InteractionResult.SUCCESS;
		return IWrenchable.super.onWrenched(state, context);
	}

	default boolean tryRemoveBracket(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Optional<ItemStack> bracket = removeBracket(world, pos, false);
		if (bracket.isPresent()) {
			Player player = context.getPlayer();
			if (!world.isClientSide && player != null && !player.isCreative())
				player.getInventory().placeItemBackInInventory(bracket.get());
			return true;
		}
		return false;
	}
}

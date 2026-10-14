package com.simibubi.create.content.redstone.contact;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneContactItem extends BlockItem {

	public RedstoneContactItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	protected BlockState getPlacementState(BlockPlaceContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		if (state == null)
			return state;
		if (!(state.getBlock() instanceof RedstoneContactBlock))
			return state;

		// TODO: Convert to ELEVATOR_CONTACT when elevator column is ported
		return state;
	}

}

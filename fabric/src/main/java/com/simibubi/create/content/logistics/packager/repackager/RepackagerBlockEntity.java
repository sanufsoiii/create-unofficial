package com.simibubi.create.content.logistics.packager.repackager;

import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RepackagerBlockEntity extends PackagerBlockEntity {

	public RepackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	@Override
	public boolean unwrapBox(ItemStack box, boolean simulate) {
		if (animationTicks > 0)
			return false;
		if (simulate)
			return true;

		previouslyUnwrapped = box;
		animationInward = true;
		animationTicks = CYCLE;
		notifyUpdate();
		return true;
	}

	@Override
	public void recheckIfLinksPresent() {
	}

	@Override
	public boolean redstoneModeActive() {
		return true;
	}

}

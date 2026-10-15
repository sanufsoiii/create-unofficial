package com.simibubi.create.content.logistics.packagePort;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public abstract class PackagePortTarget {
	public BlockPos relativePos;

	public PackagePortTarget(BlockPos relativePos) {
		this.relativePos = relativePos;
	}

	public abstract ItemStack getIcon();

	public void register(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {}

	public void deregister(PackagePortBlockEntity ppbe, LevelAccessor level, BlockPos portPos) {}
}

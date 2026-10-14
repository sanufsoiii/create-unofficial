package com.simibubi.create.content.trains.signal;

import net.minecraft.core.BlockPos;

public abstract class SingleBlockEntityEdgePoint extends TrackEdgePoint {
	private BlockPos blockEntityPos;

	public BlockPos getBlockEntityPos() {
		return blockEntityPos;
	}

	public void setBlockEntityPos(BlockPos blockEntityPos) {
		this.blockEntityPos = blockEntityPos;
	}
}

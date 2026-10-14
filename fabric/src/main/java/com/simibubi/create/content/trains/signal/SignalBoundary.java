package com.simibubi.create.content.trains.signal;

import net.minecraft.core.BlockPos;

public class SignalBoundary extends TrackEdgePoint {
	public SignalBlock.SignalType getTypeFor(BlockPos pos) {
		return SignalBlock.SignalType.ENTRY_SIGNAL;
	}

	public void cycleSignalType(BlockPos pos) {
	}
}

package com.simibubi.create.content.contraptions.elevator;

import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ElevatorPulleyBlockEntity extends PulleyBlockEntity {

	public ElevatorPulleyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void clicked() {
		// Assembly/elevator logic will be connected on Stage 2
	}
}

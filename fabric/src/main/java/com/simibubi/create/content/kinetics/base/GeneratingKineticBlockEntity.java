package com.simibubi.create.content.kinetics.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GeneratingKineticBlockEntity extends KineticBlockEntity {

	public boolean reActivateSource;

	public GeneratingKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public float getGeneratedSpeed() {
		return 0;
	}

	public void updateGeneratedRotation() {
		float speed = getGeneratedSpeed();
		setSpeed(speed);
	}
}

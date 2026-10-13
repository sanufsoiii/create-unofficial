package com.simibubi.create.content.kinetics.press;

import java.util.List;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MechanicalPressBlockEntity extends BasinOperatingBlockEntity {

	public PressingBehaviour pressingBehaviour;

	public MechanicalPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0)
			.expandTowards(0, 1, 0);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		pressingBehaviour = new PressingBehaviour(this);
		behaviours.add(pressingBehaviour);
	}

	public PressingBehaviour getPressingBehaviour() {
		return pressingBehaviour;
	}

	@Override
	protected boolean isRunning() {
		return pressingBehaviour.running;
	}

	@Override
	protected void onBasinRemoved() {
		if (pressingBehaviour.running) {
			pressingBehaviour.running = false;
			pressingBehaviour.finished = false;
		}
	}
}

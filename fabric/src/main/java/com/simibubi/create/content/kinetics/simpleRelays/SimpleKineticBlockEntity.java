package com.simibubi.create.content.kinetics.simpleRelays;

import java.util.List;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleKineticBlockEntity extends KineticBlockEntity {

	public SimpleKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
		if (!ICogWheel.isLargeCog(state))
			return neighbours;

		BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.distSqr(BlockPos.ZERO) == 2)
					neighbours.add(worldPosition.offset(offset));
			});
		return neighbours;
	}
}

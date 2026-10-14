package com.simibubi.create.content.trains.station;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StationBlockEntity extends SmartBlockEntity implements TransformableBlockEntity {

	public TrackTargetingBehaviour<GlobalStation> edgePoint;
	public DepotBehaviour depotBehaviour;
	public boolean trainPresent;

	public StationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		edgePoint = new TrackTargetingBehaviour<>(this, EdgePointType.STATION);
		behaviours.add(edgePoint);
		depotBehaviour = new DepotBehaviour(this);
		behaviours.add(depotBehaviour);
	}

	@Nullable
	public GlobalStation getStation() {
		return edgePoint.getEdgePoint();
	}

	public ItemStack getAutoSchedule() {
		return depotBehaviour.getHeldItemStack();
	}

	@Override
	public void transform(BlockEntity blockEntity, StructureTransform transform) {
	}
}

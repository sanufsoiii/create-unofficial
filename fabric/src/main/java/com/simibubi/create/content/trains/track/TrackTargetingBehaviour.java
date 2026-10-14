package com.simibubi.create.content.trains.track;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;

public class TrackTargetingBehaviour<T extends TrackEdgePoint> extends BlockEntityBehaviour {
	public static final BehaviourType<TrackTargetingBehaviour<?>> TYPE = new BehaviourType<>();

	private BlockPos targetTrack;
	private EdgePointType<T> edgePointType;
	private T edgePoint;

	public TrackTargetingBehaviour(SmartBlockEntity be, EdgePointType<T> edgePointType) {
		super(be);
		this.edgePointType = edgePointType;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@Nullable
	public T getEdgePoint() {
		return edgePoint;
	}

	public BlockPos getTargetTrack() {
		return targetTrack;
	}

	public EdgePointType<T> getEdgePointType() {
		return edgePointType;
	}
}

package com.simibubi.create.content.trains.signal;

import java.util.UUID;

import com.simibubi.create.content.trains.graph.EdgePointType;

public abstract class TrackEdgePoint {
	public UUID id;
	private EdgePointType<?> type;

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public void setType(EdgePointType<?> type) {
		this.type = type;
	}

	public EdgePointType<?> getType() {
		return type;
	}

	public boolean canMerge() {
		return false;
	}
}

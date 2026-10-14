package com.simibubi.create.content.trains.track;

import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class BezierConnection {

	public final Couple<BlockPos> bePositions;
	public final Couple<Vec3> starts;
	public final Couple<Vec3> axes;
	public final Couple<Vec3> normals;
	@Nullable
	public Couple<Integer> smoothing;
	public final boolean primary;
	public final boolean hasGirder;
	protected TrackMaterial trackMaterial;

	public BezierConnection(Couple<BlockPos> positions, Couple<Vec3> starts, Couple<Vec3> axes, Couple<Vec3> normals,
		boolean primary, boolean girder, TrackMaterial material) {
		this.bePositions = positions;
		this.starts = starts;
		this.axes = axes;
		this.normals = normals;
		this.primary = primary;
		this.hasGirder = girder;
		this.trackMaterial = material;
	}

	public TrackMaterial getMaterial() {
		return trackMaterial;
	}

	public void setMaterial(TrackMaterial trackMaterial) {
		this.trackMaterial = trackMaterial;
	}

	public BlockPos getKey() {
		return bePositions.getSecond();
	}

	public CompoundTag write() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("Primary", primary);
		tag.putBoolean("Girder", hasGirder);
		return tag;
	}
}

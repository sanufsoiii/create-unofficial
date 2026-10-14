package com.simibubi.create.content.trains.track;

import java.util.List;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface ITrackBlock {

	Vec3 getUpNormal(BlockGetter world, BlockPos pos, BlockState state);

	List<Vec3> getTrackAxes(BlockGetter world, BlockPos pos, BlockState state);

	Vec3 getCurveStart(BlockGetter world, BlockPos pos, BlockState state, Vec3 axis);

	default int getYOffsetAt(BlockGetter world, BlockPos pos, BlockState state, Vec3 end) {
		return 0;
	}

	BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state);

	boolean trackEquals(BlockState state1, BlockState state2);

	default BlockState overlay(BlockGetter world, BlockPos pos, BlockState existing, BlockState placed) {
		return existing;
	}

	default double getElevationAtCenter(BlockGetter world, BlockPos pos, BlockState state) {
		return isSlope(world, pos, state) ? .5 : 0;
	}

	static TrackMaterial getMaterialSimple(BlockGetter world, Vec3 pos) {
		return getMaterialSimple(world, pos, TrackMaterial.ANDESITE);
	}

	static TrackMaterial getMaterialSimple(BlockGetter world, Vec3 pos, TrackMaterial defaultMaterial) {
		if (defaultMaterial == null)
			defaultMaterial = TrackMaterial.ANDESITE;
		if (world != null) {
			Block block = world.getBlockState(BlockPos.containing(pos)).getBlock();
			if (block instanceof ITrackBlock track) {
				return track.getMaterial();
			}
		}
		return defaultMaterial;
	}

	default boolean isSlope(BlockGetter world, BlockPos pos, BlockState state) {
		return getTrackAxes(world, pos, state).get(0).y != 0;
	}

	default Pair<Vec3, AxisDirection> getNearestTrackAxis(BlockGetter world, BlockPos pos, BlockState state,
														 Vec3 lookVec) {
		Vec3 best = null;
		double bestDiff = Double.MAX_VALUE;
		for (Vec3 vec3 : getTrackAxes(world, pos, state)) {
			for (int opposite : Iterate.positiveAndNegative) {
				double distanceTo = vec3.normalize()
					.distanceTo(lookVec.scale(opposite));
				if (distanceTo > bestDiff)
					continue;
				bestDiff = distanceTo;
				best = vec3;
			}
		}
		return Pair.of(best, lookVec.dot(best.multiply(1, 0, 1)
			.normalize()) < 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE);
	}

	TrackMaterial getMaterial();
}

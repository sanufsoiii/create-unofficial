package com.simibubi.create.content.trains.track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.IMergeableBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TrackBlockEntity extends SmartBlockEntity implements TransformableBlockEntity, IMergeableBE {

	Map<BlockPos, BezierConnection> connections = new HashMap<>();
	public boolean cancelDrops;
	public Pair<ResourceKey<Level>, BlockPos> boundLocation;

	public TrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public Map<BlockPos, BezierConnection> getConnections() {
		return connections;
	}

	public boolean isTilted() {
		return false;
	}

	@Override
	public void transform(net.minecraft.world.level.block.entity.BlockEntity blockEntity, StructureTransform transform) {
	}

	@Override
	public void accept(net.minecraft.world.level.block.entity.BlockEntity other) {
	}
}

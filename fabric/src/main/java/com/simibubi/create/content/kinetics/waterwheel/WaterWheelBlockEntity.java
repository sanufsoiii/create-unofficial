package com.simibubi.create.content.kinetics.waterwheel;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WaterWheelBlockEntity extends GeneratingKineticBlockEntity {

	public static final Map<Axis, Set<BlockPos>> SMALL_OFFSETS = new EnumMap<>(Axis.class);
	public static final Map<Axis, Set<BlockPos>> LARGE_OFFSETS = new EnumMap<>(Axis.class);

	static {
		for (Axis axis : Iterate.axes) {
			HashSet<BlockPos> offsets = new HashSet<>();
			for (Direction d : Iterate.directions)
				if (d.getAxis() != axis)
					offsets.add(BlockPos.ZERO.relative(d));
			SMALL_OFFSETS.put(axis, offsets);

			offsets = new HashSet<>();
			for (Direction d : Iterate.directions) {
				if (d.getAxis() == axis)
					continue;
				BlockPos centralOffset = BlockPos.ZERO.relative(d, 2);
				offsets.add(centralOffset);
				for (Direction d2 : Iterate.directions) {
					if (d2.getAxis() == axis)
						continue;
					if (d2.getAxis() == d.getAxis())
						continue;
					offsets.add(centralOffset.relative(d2));
				}
			}
			LARGE_OFFSETS.put(axis, offsets);
		}
	}

	public int flowScore;
	public BlockState material;

	public WaterWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		material = Blocks.SPRUCE_PLANKS.defaultBlockState();
	}

	protected int getSize() {
		return 1;
	}

	protected Set<BlockPos> getOffsetsToCheck() {
		return (getSize() == 1 ? SMALL_OFFSETS : LARGE_OFFSETS).get(getAxis());
	}

	public ItemInteractionResult applyMaterialIfValid(ItemStack stack) {
		if (!(stack.getItem() instanceof BlockItem blockItem))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		BlockState material = blockItem.getBlock().defaultBlockState();
		if (material == this.material)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (!material.is(BlockTags.PLANKS))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (level != null && level.isClientSide())
			return ItemInteractionResult.SUCCESS;
		this.material = material;
		setChanged();
		if (level != null)
			level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, worldPosition, Block.getId(material));
		return ItemInteractionResult.SUCCESS;
	}

	protected Axis getAxis() {
		Axis axis = Axis.X;
		BlockState blockState = getBlockState();
		if (blockState.getBlock() instanceof IRotate irotate)
			axis = irotate.getRotationAxis(blockState);
		return axis;
	}

	public void determineAndApplyFlowScore() {
		if (level == null)
			return;

		Vec3 wheelPlane =
			Vec3.atLowerCornerOf(new Vec3i(1, 1, 1).subtract(Direction.get(AxisDirection.POSITIVE, getAxis())
				.getNormal()));

		int flowScore = 0;
		for (BlockPos blockPos : getOffsetsToCheck()) {
			BlockPos targetPos = blockPos.offset(worldPosition);
			Vec3 flowAtPos = getFlowVectorAtPosition(targetPos).multiply(wheelPlane);

			if (flowAtPos.lengthSqr() == 0)
				continue;

			flowAtPos = flowAtPos.normalize();
			Vec3 normal = Vec3.atLowerCornerOf(blockPos).normalize();

			Vec3 positiveMotion = VecHelper.rotate(normal, 90, getAxis());
			double dot = flowAtPos.dot(positiveMotion);
			if (Math.abs(dot) > .5)
				flowScore += Math.signum(dot);
		}

		setFlowScoreAndUpdate(flowScore);
	}

	public Vec3 getFlowVectorAtPosition(BlockPos pos) {
		if (level == null)
			return Vec3.ZERO;
		FluidState fluid = level.getFluidState(pos);
		Vec3 vec = fluid.getFlow(level, pos);
		BlockState blockState = level.getBlockState(pos);
		if (blockState.getBlock() == Blocks.BUBBLE_COLUMN)
			vec = new Vec3(0, blockState.getValue(BubbleColumnBlock.DRAG_DOWN) ? -1 : 1, 0);
		return vec;
	}

	public void setFlowScoreAndUpdate(int score) {
		if (flowScore == score)
			return;
		flowScore = score;
		updateGeneratedRotation();
		setChanged();
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		flowScore = compound.getInt("FlowScore");

		if (!compound.contains("Material"))
			return;

		material = NbtUtils.readBlockState(level != null ? level.holderLookup(net.minecraft.core.registries.Registries.BLOCK) : registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK), compound.getCompound("Material"));
		if (material.isAir())
			material = Blocks.SPRUCE_PLANKS.defaultBlockState();
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.putInt("FlowScore", flowScore);
		if (material != null)
			compound.put("Material", NbtUtils.writeBlockState(material));
	}

	@Override
	public float getGeneratedSpeed() {
		return Mth.clamp(flowScore, -1, 1) * 8 / getSize();
	}

}

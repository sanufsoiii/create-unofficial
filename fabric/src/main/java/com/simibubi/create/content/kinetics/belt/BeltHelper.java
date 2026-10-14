package com.simibubi.create.content.kinetics.belt;

import java.util.Map;

import com.simibubi.create.AllTags.AllItemTags;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BeltHelper {

	public static Map<Item, Boolean> uprightCache = new Object2BooleanOpenHashMap<>();

	public static boolean isItemUpright(ItemStack stack) {
		return uprightCache.computeIfAbsent(stack.getItem(),
			item -> ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null
				|| AllItemTags.UPRIGHT_ON_BELT.matches(stack));
	}

	public static BeltBlockEntity getSegmentBE(LevelAccessor world, BlockPos pos) {
		if (world instanceof Level l && !l.isLoaded(pos))
			return null;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof BeltBlockEntity))
			return null;
		return (BeltBlockEntity) blockEntity;
	}

	public static BeltBlockEntity getControllerBE(LevelAccessor world, BlockPos pos) {
		BeltBlockEntity segment = getSegmentBE(world, pos);
		if (segment == null)
			return null;
		BlockPos controllerPos = segment.controller;
		if (controllerPos == null)
			return null;
		return getSegmentBE(world, controllerPos);
	}

	public static BeltBlockEntity getBeltForOffset(BeltBlockEntity controller, float offset) {
		return getBeltAtSegment(controller, (int) Math.floor(offset));
	}

	public static BeltBlockEntity getBeltAtSegment(BeltBlockEntity controller, int segment) {
		BlockPos pos = getPositionForOffset(controller, segment);
		BlockEntity be = controller.getLevel()
			.getBlockEntity(pos);
		if (be == null || !(be instanceof BeltBlockEntity))
			return null;
		return (BeltBlockEntity) be;
	}

	public static BlockPos getPositionForOffset(BeltBlockEntity controller, int offset) {
		BlockPos pos = controller.getBlockPos();
		net.minecraft.core.Vec3i vec = controller.getBeltFacing()
			.getNormal();
		BeltSlope slope = controller.getBlockState()
			.getValue(BeltBlock.SLOPE);
		int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;

		return pos.offset(offset * vec.getX(), net.minecraft.util.Mth.clamp(offset, 0, controller.beltLength - 1) * verticality,
			offset * vec.getZ());
	}

	public static net.minecraft.world.phys.Vec3 getVectorForOffset(BeltBlockEntity controller, float offset) {
		BeltSlope slope = controller.getBlockState()
			.getValue(BeltBlock.SLOPE);
		int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;
		float verticalMovement = verticality;
		if (offset < .5)
			verticalMovement = 0;
		verticalMovement = verticalMovement * (Math.min(offset, controller.beltLength - .5f) - .5f);
		net.minecraft.world.phys.Vec3 vec = net.createmod.catnip.math.VecHelper.getCenterOf(controller.getBlockPos());
		net.minecraft.world.phys.Vec3 horizontalMovement = net.minecraft.world.phys.Vec3.atLowerCornerOf(controller.getBeltFacing()
			.getNormal())
			.scale(offset - .5f);

		if (slope == BeltSlope.VERTICAL)
			horizontalMovement = net.minecraft.world.phys.Vec3.ZERO;

		vec = vec.add(horizontalMovement)
			.add(0, verticalMovement, 0);
		return vec;
	}

	public static net.minecraft.world.phys.Vec3 getBeltVector(net.minecraft.world.level.block.state.BlockState state) {
		BeltSlope slope = state.getValue(BeltBlock.SLOPE);
		int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;
		net.minecraft.world.phys.Vec3 horizontalMovement = net.minecraft.world.phys.Vec3.atLowerCornerOf(state.getValue(BeltBlock.HORIZONTAL_FACING)
			.getNormal());
		if (slope == BeltSlope.VERTICAL)
			return new net.minecraft.world.phys.Vec3(0, state.getValue(BeltBlock.HORIZONTAL_FACING)
				.getAxisDirection()
				.getStep(), 0);
		return new net.minecraft.world.phys.Vec3(0, verticality, 0).add(horizontalMovement);
	}

}

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

}

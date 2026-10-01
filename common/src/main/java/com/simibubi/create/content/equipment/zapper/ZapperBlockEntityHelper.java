package com.simibubi.create.content.equipment.zapper;

import com.simibubi.create.Create;

import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ZapperBlockEntityHelper {
	public static final TagKey<Block> SAFE_NBT = TagKey.create(Registries.BLOCK, Create.asResource("safe_nbt"));

	public static void setBlockEntityData(Level world, BlockPos pos, BlockState state, CompoundTag data, Player player) {
		if (data != null && state.is(SAFE_NBT)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity != null) {
				data = NBTProcessors.process(state, blockEntity, data, !player.isCreative());
				if (data == null)
					return;
				data.putInt("x", pos.getX());
				data.putInt("y", pos.getY());
				data.putInt("z", pos.getZ());
				blockEntity.loadWithComponents(data, world.registryAccess());
			}
		}
	}
}

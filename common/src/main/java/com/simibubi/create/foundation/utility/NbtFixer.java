package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

public class NbtFixer {

	public static BlockPos readBlockPos(CompoundTag tag, String key) {
		Optional<BlockPos> opt = NbtUtils.readBlockPos(tag, key);
		if (opt.isEmpty())
			return readBlockPos(tag.getCompound(key));
		return opt.get();
	}

	public static BlockPos readBlockPos(int[] arr) {
		if (arr != null && arr.length == 3) {
			return new BlockPos(arr[0], arr[1], arr[2]);
		}
		return null;
	}

	public static BlockPos readBlockPos(CompoundTag tag) {
		return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
	}

	public static List<BlockPos> readBlockPosList(CompoundTag compound, String name) {
		ListTag tagList = compound.getList(name, Tag.TAG_INT_ARRAY);
		if (tagList == null || tagList.size() == 0) {
			tagList = compound.getList(name, Tag.TAG_COMPOUND);
			return NBTHelper.readCompoundList(tagList, NbtFixer::readBlockPos);
		} else {
			final List<BlockPos> result = new ArrayList<>();
			tagList.forEach(tag -> {
				result.add(NbtFixer.readBlockPos(((IntArrayTag) tag).getAsIntArray()));
			});
			return result;
		}
	}

	public static void writeBlockPosList(CompoundTag compound, String name, List<BlockPos> tagList) {
		ListTag listNBT = new ListTag();
		tagList.forEach(t -> {
			listNBT.add(NbtUtils.writeBlockPos(t));
		});
		compound.put(name, listNBT);
	}

}

package com.simibubi.create.content.schematics;

import java.util.Iterator;

import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SchematicAndQuillItem extends Item {

	public SchematicAndQuillItem(Properties properties) {
		super(properties);
	}

	public static void replaceStructureVoidWithAir(CompoundTag nbt) {
		String air = RegisteredObjectsHelper.getKeyOrThrow(Blocks.AIR)
			.toString();
		String structureVoid = RegisteredObjectsHelper.getKeyOrThrow(Blocks.STRUCTURE_VOID)
			.toString();

		NBTHelper.iterateCompoundList(nbt.getList("palette", 10), c -> {
			if (c.contains("Name") && c.getString("Name")
				.equals(structureVoid)) {
				c.putString("Name", air);
			}
		});
	}

	public static void clampGlueBoxes(Level level, AABB aabb, CompoundTag nbt) {
		// Glue clamping stub until SuperGlue is ported
	}

	private static ListTag newIntegerList(int... pValues) {
		ListTag listtag = new ListTag();
		for (int i : pValues)
			listtag.add(IntTag.valueOf(i));
		return listtag;
	}

	private static ListTag newDoubleList(double... pValues) {
		ListTag listtag = new ListTag();
		for (double d0 : pValues)
			listtag.add(DoubleTag.valueOf(d0));
		return listtag;
	}

}

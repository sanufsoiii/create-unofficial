package com.simibubi.create.content.kinetics.belt.transport;

import java.util.Random;

import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.logistics.box.PackageItemHelper;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class TransportedItemStack implements Comparable<TransportedItemStack> {

	private static final Random R = new Random();

	public static final TransportedItemStack EMPTY = new TransportedItemStack(ItemStack.EMPTY);

	public ItemStack stack;
	public float beltPosition;
	public float sideOffset;
	public int angle;
	public int insertedAt;
	public Direction insertedFrom;
	public boolean locked;
	public boolean lockedExternally;

	public float prevBeltPosition;
	public float prevSideOffset;

	public Object processedBy;
	public int processingTime;

	public TransportedItemStack(ItemStack stack) {
		this.stack = stack;
		boolean centered = BeltHelper.isItemUpright(stack);
		angle = centered ? 180 : R.nextInt(360);
		if (PackageItemHelper.isPackage(stack))
			angle = R.nextInt(4) * 90 + R.nextInt(20) - 10;
		sideOffset = prevSideOffset = getTargetSideOffset();
		insertedFrom = Direction.UP;
	}

	public float getTargetSideOffset() {
		return (angle - 180) / (360 * 3f);
	}

	@Override
	public int compareTo(TransportedItemStack o) {
		return beltPosition < o.beltPosition ? 1 : beltPosition > o.beltPosition ? -1 : 0;
	}

	public TransportedItemStack getSimilar() {
		TransportedItemStack copy = new TransportedItemStack(stack.copy());
		copy.beltPosition = beltPosition;
		copy.insertedAt = insertedAt;
		copy.insertedFrom = insertedFrom;
		copy.prevBeltPosition = prevBeltPosition;
		copy.prevSideOffset = prevSideOffset;
		copy.processedBy = processedBy;
		copy.processingTime = processingTime;
		return copy;
	}

	public TransportedItemStack copy() {
		TransportedItemStack copy = getSimilar();
		copy.angle = angle;
		copy.sideOffset = sideOffset;
		return copy;
	}

	public TransportedItemStack fullCopy() {
		TransportedItemStack copy = copy();
		copy.locked = locked;
		copy.lockedExternally = lockedExternally;
		return copy;
	}

	public CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag nbt = new CompoundTag();
		nbt.put("Item", stack.saveOptional(registries));
		nbt.putFloat("Pos", beltPosition);
		nbt.putFloat("PrevPos", prevBeltPosition);
		nbt.putFloat("Offset", sideOffset);
		nbt.putFloat("PrevOffset", prevSideOffset);
		nbt.putInt("InSegment", insertedAt);
		nbt.putInt("Angle", angle);
		nbt.putInt("InDirection", insertedFrom.get3DDataValue());
		if (locked)
			nbt.putBoolean("Locked", locked);
		if (lockedExternally)
			nbt.putBoolean("LockedExternally", lockedExternally);
		return nbt;
	}

	public static TransportedItemStack read(CompoundTag nbt, HolderLookup.Provider registries) {
		TransportedItemStack stack = new TransportedItemStack(ItemStack.parseOptional(registries, nbt.getCompound("Item")));
		stack.beltPosition = nbt.getFloat("Pos");
		stack.prevBeltPosition = nbt.getFloat("PrevPos");
		stack.sideOffset = nbt.getFloat("Offset");
		stack.prevSideOffset = nbt.getFloat("PrevOffset");
		stack.insertedAt = nbt.getInt("InSegment");
		stack.angle = nbt.getInt("Angle");
		stack.insertedFrom = Direction.from3DDataValue(nbt.getInt("InDirection"));
		stack.locked = nbt.getBoolean("Locked");
		stack.lockedExternally = nbt.getBoolean("LockedExternally");
		return stack;
	}

	public void clearFanProcessingData() {
		processedBy = null;
		processingTime = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TransportedItemStack that = (TransportedItemStack) o;

		if (Float.compare(that.beltPosition, beltPosition) != 0)
			return false;
		if (Float.compare(that.sideOffset, sideOffset) != 0)
			return false;
		if (angle != that.angle)
			return false;
		if (insertedAt != that.insertedAt)
			return false;
		if (locked != that.locked)
			return false;
		if (lockedExternally != that.lockedExternally)
			return false;
		if (Float.compare(that.prevBeltPosition, prevBeltPosition) != 0)
			return false;
		if (Float.compare(that.prevSideOffset, prevSideOffset) != 0)
			return false;
		if (processingTime != that.processingTime)
			return false;
		if (!ItemStack.matches(stack, that.stack))
			return false;
		return insertedFrom == that.insertedFrom;
	}
}

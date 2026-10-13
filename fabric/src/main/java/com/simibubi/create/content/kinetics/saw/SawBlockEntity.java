package com.simibubi.create.content.kinetics.saw;

import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SawBlockEntity extends BlockBreakingKineticBlockEntity implements Clearable {

	public ProcessingInventory inventory;

	public SawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ProcessingInventory(this::start);
		inventory.remainingTime = -1;
	}

	public void start(ItemStack inserted) {
	}

	public void insertItem(ItemEntity entity) {
		if (getSpeed() == 0)
			return;
		if (!inventory.empty())
			return;
		if (!entity.isAlive())
			return;
		if (level.isClientSide)
			return;

		ItemStack contained = entity.getItem();
		inventory.setStackInSlot(0, contained.copy());
		entity.discard();
	}

	protected boolean canProcess() {
		return getBlockState().getValue(SawBlock.FACING) == Direction.UP;
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	@Override
	protected BlockPos getBreakingPos() {
		return getBlockPos().relative(getBlockState().getValue(SawBlock.FACING));
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.put("Inventory", inventory.serializeNBT(registries));
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (compound.contains("Inventory"))
			inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(.125f);
	}

}

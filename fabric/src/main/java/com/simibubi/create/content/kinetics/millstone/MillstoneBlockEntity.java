package com.simibubi.create.content.kinetics.millstone;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.math.VecHelper;

import io.github.fabricators_of_create.porting_lib.transfer.ViewOnlyWrappedStorageView;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MillstoneBlockEntity extends KineticBlockEntity implements SidedStorageBlockEntity, Clearable {

	public ItemStackHandlerContainer inputInv;
	public ItemStackHandler outputInv;
	public MillstoneInventoryHandler capability;
	public int timer;

	public MillstoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inputInv = new ItemStackHandlerContainer(1);
		outputInv = new ItemStackHandler(9);
		capability = new MillstoneInventoryHandler();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < inputInv.getSlotCount(); i++)
			inputInv.setStackInSlot(i, ItemStack.EMPTY);
		for (int i = 0; i < outputInv.getSlotCount(); i++)
			outputInv.setStackInSlot(i, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		for (int i = 0; i < outputInv.getSlotCount(); i++)
			if (outputInv.getStackInSlot(i).getCount() == outputInv.getSlotLimit(i))
				return;

		if (inputInv.getStackInSlot(0).isEmpty())
			return;

		if (timer > 0) {
			timer -= getProcessingSpeed();
			if (level.isClientSide) {
				spawnParticles();
				return;
			}
			return;
		}

		timer = 100;
	}

	public void spawnParticles() {
		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		if (stackInSlot.isEmpty())
			return;

		ItemParticleOption data = new ItemParticleOption(ParticleTypes.ITEM, stackInSlot);
		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.5f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y).add(0, .25f, 0);

		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putInt("Timer", timer);
		compound.put("InputInventory", inputInv.serializeNBT(registries));
		compound.put("OutputInventory", outputInv.serializeNBT(registries));
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		timer = compound.getInt("Timer");
		inputInv.deserializeNBT(registries, compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(registries, compound.getCompound("OutputInventory"));
		super.read(compound, registries, clientPacket);
	}

	public int getProcessingSpeed() {
		return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction direction) {
		return capability;
	}

	private boolean canProcess(ItemStack stack) {
		return !stack.isEmpty();
	}

	public class MillstoneInventoryHandler extends CombinedStorage<ItemVariant, ItemStackHandler> {

		public MillstoneInventoryHandler() {
			super(List.of(inputInv, outputInv));
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (canProcess(resource.toStack()))
				return inputInv.insert(resource, maxAmount, transaction);
			return 0;
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return outputInv.extract(resource, maxAmount, transaction);
		}

		@Override
		public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
			return new MillstoneInventoryHandlerIterator();
		}

		private class MillstoneInventoryHandlerIterator implements Iterator<StorageView<ItemVariant>> {
			private boolean output = true;
			private Iterator<StorageView<ItemVariant>> wrapped;

			public MillstoneInventoryHandlerIterator() {
				wrapped = outputInv.iterator();
			}

			@Override
			public boolean hasNext() {
				return wrapped.hasNext();
			}

			@Override
			public StorageView<ItemVariant> next() {
				StorageView<ItemVariant> view = wrapped.next();
				if (!output) view = new ViewOnlyWrappedStorageView<>(view);
				if (output && !hasNext()) {
					wrapped = inputInv.iterator();
					output = false;
				}
				return view;
			}
		}
	}
}

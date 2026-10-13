package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dev.architectury.fluid.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class SmartFluidTankHooksImpl {

	public static SmartFluidTankBehaviour.TankSegment createTankSegment(SmartFluidTankBehaviour behaviour, int tankCapacity) {
		return new FabricTankSegment(behaviour, tankCapacity);
	}

	public static Object createHandler(SmartFluidTankBehaviour behaviour, SmartFluidTankBehaviour.TankSegment[] tanks, boolean enforceVariety) {
		java.util.List<net.fabricmc.fabric.api.transfer.v1.storage.Storage<FluidVariant>> storages = new java.util.ArrayList<>();
		for (SmartFluidTankBehaviour.TankSegment segment : tanks) {
			if (segment instanceof FabricTankSegment fabricSegment) {
				storages.add(fabricSegment.storage);
			}
		}
		return new net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage<>(storages);
	}

	public static void invalidateCapabilities(SmartBlockEntity blockEntity) {
	}

	public static class FabricTankSegment extends SmartFluidTankBehaviour.TankSegment {
		protected final SingleVariantStorage<FluidVariant> storage;
		protected FluidStack renderedFluid;
		protected final int capacity;

		public FabricTankSegment(SmartFluidTankBehaviour behaviour, int capacity) {
			super(behaviour);
			this.capacity = capacity;
			this.renderedFluid = FluidStack.empty();
			this.storage = new SingleVariantStorage<>() {
				@Override
				protected FluidVariant getBlankVariant() {
					return FluidVariant.blank();
				}

				@Override
				protected long getCapacity(FluidVariant variant) {
					return capacity;
				}

				@Override
				protected void onFinalCommit() {
					onFluidStackChanged();
				}
			};
		}

		@Override
		public void onFluidStackChanged() {
			if (!behaviour.blockEntity.hasLevel())
				return;
			fluidLevel.chase(storage.amount / (float) capacity, .25, net.createmod.catnip.animation.LerpedFloat.Chaser.EXP);
			if (!behaviour.getWorld().isClientSide)
				behaviour.sendDataLazily();
		}

		@SuppressWarnings("unchecked")
		@Override
		public FluidStack getRenderedFluid() {
			return renderedFluid;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getTank() {
			return storage;
		}

		@Override
		public float getTotalUnits(float partialTicks) {
			return fluidLevel.getValue(partialTicks) * capacity;
		}

		@Override
		public boolean isEmpty(float partialTicks) {
			return storage.amount <= 0;
		}

		@Override
		public boolean isEmpty() {
			return storage.amount <= 0;
		}

		@Override
		public void writeTankNBT(CompoundTag compound, HolderLookup.Provider registries) {
			CompoundTag tankTag = new CompoundTag();
			tankTag.putLong("Amount", storage.amount);
			compound.put("TankContent", tankTag);
		}

		@Override
		public void readTankNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
			CompoundTag tankTag = compound.getCompound("TankContent");
			storage.amount = tankTag.getLong("Amount");
		}
	}
}

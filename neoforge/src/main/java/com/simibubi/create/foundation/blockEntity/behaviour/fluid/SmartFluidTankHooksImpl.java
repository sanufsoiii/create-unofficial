package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class SmartFluidTankHooksImpl {

	public static SmartFluidTankBehaviour.TankSegment createTankSegment(SmartFluidTankBehaviour behaviour, int tankCapacity) {
		return new NeoTankSegment(behaviour, tankCapacity);
	}

	public static Object createHandler(SmartFluidTankBehaviour behaviour, SmartFluidTankBehaviour.TankSegment[] tanks, boolean enforceVariety) {
		IFluidHandler[] handlers = new IFluidHandler[tanks.length];
		for (int i = 0; i < tanks.length; i++) {
			handlers[i] = tanks[i].getTank();
		}
		return new InternalFluidHandler(behaviour, handlers, enforceVariety);
	}

	public static void invalidateCapabilities(SmartBlockEntity blockEntity) {
		if (blockEntity.hasLevel()) {
			blockEntity.getLevel().invalidateCapabilities(blockEntity.getBlockPos());
		}
	}

	public static class InternalFluidHandler extends CombinedTankWrapper implements SmartFluidTankBehaviour.InternalFluidHandler {
		private final SmartFluidTankBehaviour behaviour;

		public InternalFluidHandler(SmartFluidTankBehaviour behaviour, IFluidHandler[] handlers, boolean enforceVariety) {
			super(handlers);
			this.behaviour = behaviour;
			if (enforceVariety)
				enforceVariety();
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (!behaviour.isInsertionAllowed())
				return 0;
			return super.fill(resource, action);
		}

		public int forceFill(FluidStack resource, FluidAction action) {
			return super.fill(resource, action);
		}

		@Override
		public int forceFill(Object resource, Object action) {
			return forceFill((FluidStack) resource, (FluidAction) action);
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (!behaviour.isExtractionAllowed())
				return FluidStack.EMPTY;
			return super.drain(resource, action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (!behaviour.isExtractionAllowed())
				return FluidStack.EMPTY;
			return super.drain(maxDrain, action);
		}
	}

	public static class NeoTankSegment extends SmartFluidTankBehaviour.TankSegment {
		protected SmartFluidTank tank;
		protected FluidStack renderedFluid;

		public NeoTankSegment(SmartFluidTankBehaviour behaviour, int capacity) {
			super(behaviour);
			tank = new SmartFluidTank(capacity, f -> onFluidStackChanged());
			renderedFluid = FluidStack.EMPTY;
		}

		@Override
		public void onFluidStackChanged() {
			if (!behaviour.blockEntity.hasLevel())
				return;
			fluidLevel.chase(tank.getFluidAmount() / (float) tank.getCapacity(), .25, Chaser.EXP);
			if (!behaviour.getWorld().isClientSide)
				behaviour.sendDataLazily();
			if (behaviour.blockEntity.isVirtual() && !tank.getFluid().isEmpty())
				renderedFluid = tank.getFluid();
		}

		@SuppressWarnings("unchecked")
		@Override
		public FluidStack getRenderedFluid() {
			return renderedFluid;
		}

		@SuppressWarnings("unchecked")
		@Override
		public SmartFluidTank getTank() {
			return tank;
		}

		@Override
		public float getTotalUnits(float partialTicks) {
			return fluidLevel.getValue(partialTicks) * tank.getCapacity();
		}

		@Override
		public boolean isEmpty(float partialTicks) {
			FluidStack renderedFluid = getRenderedFluid();
			if (renderedFluid.isEmpty())
				return true;
			float units = getTotalUnits(partialTicks);
			return units < 1;
		}

		@Override
		public boolean isEmpty() {
			return tank.isEmpty();
		}

		@Override
		public void writeTankNBT(CompoundTag compound, HolderLookup.Provider registries) {
			compound.put("TankContent", tank.writeToNBT(registries, new CompoundTag()));
		}

		@Override
		public void readTankNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
			tank.readFromNBT(registries, compound.getCompound("TankContent"));
			if (!tank.getFluid().isEmpty())
				renderedFluid = tank.getFluid();
		}
	}
}

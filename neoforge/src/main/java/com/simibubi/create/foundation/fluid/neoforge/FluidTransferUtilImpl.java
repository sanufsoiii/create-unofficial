package com.simibubi.create.foundation.fluid.neoforge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidTransferUtilImpl {

	public static long fill(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		if (resource.isEmpty())
			return 0;
		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
		if (handler == null)
			return 0;
		net.neoforged.neoforge.fluids.FluidStack neoStack = FluidStackHooksForge.toForge(resource);
		IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
		return handler.fill(neoStack, action);
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		if (resource.isEmpty())
			return FluidStack.empty();
		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
		if (handler == null)
			return FluidStack.empty();
		net.neoforged.neoforge.fluids.FluidStack neoStack = FluidStackHooksForge.toForge(resource);
		IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
		net.neoforged.neoforge.fluids.FluidStack drained = handler.drain(neoStack, action);
		return FluidStackHooksForge.fromForge(drained);
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, long maxDrain, boolean simulate) {
		if (maxDrain <= 0)
			return FluidStack.empty();
		IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
		if (handler == null)
			return FluidStack.empty();
		IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
		net.neoforged.neoforge.fluids.FluidStack drained = handler.drain((int) Math.min(maxDrain, Integer.MAX_VALUE), action);
		return FluidStackHooksForge.fromForge(drained);
	}

	public static boolean hasFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
		return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side) != null;
	}
}

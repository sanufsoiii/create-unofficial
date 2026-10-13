package com.simibubi.create.foundation.fluid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.utility.PlatformDispatch;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FluidTransferUtil {

	public static long fill(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		return (long) PlatformDispatch.call(
			"com.simibubi.create.foundation.fluid.neoforge.FluidTransferUtilImpl",
			"com.simibubi.create.foundation.fluid.fabric.FluidTransferUtilImpl",
			"fill",
			new Class<?>[]{Level.class, BlockPos.class, Direction.class, FluidStack.class, boolean.class},
			level, pos, side, resource, simulate
		);
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		return (FluidStack) PlatformDispatch.call(
			"com.simibubi.create.foundation.fluid.neoforge.FluidTransferUtilImpl",
			"com.simibubi.create.foundation.fluid.fabric.FluidTransferUtilImpl",
			"drain",
			new Class<?>[]{Level.class, BlockPos.class, Direction.class, FluidStack.class, boolean.class},
			level, pos, side, resource, simulate
		);
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, long maxDrain, boolean simulate) {
		return (FluidStack) PlatformDispatch.call(
			"com.simibubi.create.foundation.fluid.neoforge.FluidTransferUtilImpl",
			"com.simibubi.create.foundation.fluid.fabric.FluidTransferUtilImpl",
			"drain",
			new Class<?>[]{Level.class, BlockPos.class, Direction.class, long.class, boolean.class},
			level, pos, side, maxDrain, simulate
		);
	}

	public static boolean hasFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
		return (boolean) PlatformDispatch.call(
			"com.simibubi.create.foundation.fluid.neoforge.FluidTransferUtilImpl",
			"com.simibubi.create.foundation.fluid.fabric.FluidTransferUtilImpl",
			"hasFluidHandler",
			new Class<?>[]{Level.class, BlockPos.class, Direction.class},
			level, pos, side
		);
	}

	public static @NotNull FluidStack getContainedFluid(Level level, ItemStack stack) {
		return (FluidStack) PlatformDispatch.call(
			"com.simibubi.create.foundation.fluid.neoforge.FluidTransferUtilImpl",
			"com.simibubi.create.foundation.fluid.fabric.FluidTransferUtilImpl",
			"getContainedFluid",
			new Class<?>[]{Level.class, ItemStack.class},
			level, stack
		);
	}
}

package com.simibubi.create.foundation.fluid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.architectury.fluid.FluidStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FluidTransferUtil {

	@ExpectPlatform
	public static long fill(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, long maxDrain, boolean simulate) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean hasFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static @NotNull FluidStack getContainedFluid(Level level, ItemStack stack) {
		throw new AssertionError();
	}
}

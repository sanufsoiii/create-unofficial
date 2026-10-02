package com.simibubi.create.foundation.fluid.fabric;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class FluidTransferUtilImpl {

	public static long fill(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		if (resource.isEmpty())
			return 0;
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
		if (storage == null || !storage.supportsInsertion())
			return 0;

		FluidVariant variant = FluidStackHooksFabric.toFabric(resource);
		long droplets = resource.getAmount();
		try (Transaction tx = Transaction.openOuter()) {
			long insertedDroplets = storage.insert(variant, droplets, tx);
			if (!simulate) {
				tx.commit();
			}
			return insertedDroplets;
		}
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, @NotNull FluidStack resource, boolean simulate) {
		if (resource.isEmpty())
			return FluidStack.empty();
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
		if (storage == null || !storage.supportsExtraction())
			return FluidStack.empty();

		FluidVariant variant = FluidStackHooksFabric.toFabric(resource);
		long droplets = resource.getAmount();
		try (Transaction tx = Transaction.openOuter()) {
			long extractedDroplets = storage.extract(variant, droplets, tx);
			if (!simulate) {
				tx.commit();
			}
			if (extractedDroplets <= 0)
				return FluidStack.empty();
			return resource.copyWithAmount(extractedDroplets);
		}
	}

	public static @NotNull FluidStack drain(Level level, BlockPos pos, @Nullable Direction side, long maxDrain, boolean simulate) {
		if (maxDrain <= 0)
			return FluidStack.empty();
		Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
		if (storage == null || !storage.supportsExtraction())
			return FluidStack.empty();

		try (Transaction tx = Transaction.openOuter()) {
			net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount<FluidVariant> content = StorageUtil.findExtractableContent(storage, tx);
			if (content == null || content.resource().isBlank())
				return FluidStack.empty();
			FluidVariant variant = content.resource();
			long extractedDroplets = storage.extract(variant, maxDrain, tx);
			if (!simulate) {
				tx.commit();
			}
			if (extractedDroplets <= 0)
				return FluidStack.empty();
			return FluidStackHooksFabric.fromFabric(variant, extractedDroplets);
		}
	}

	public static boolean hasFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
		return FluidStorage.SIDED.find(level, pos, side) != null;
	}

	public static @NotNull FluidStack getContainedFluid(Level level, net.minecraft.world.item.ItemStack stack) {
		if (stack.isEmpty())
			return FluidStack.empty();
		net.fabricmc.fabric.api.transfer.v1.item.ItemVariant itemVariant = net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(stack);
		Storage<FluidVariant> storage = net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.withConstant(itemVariant, 1).find(FluidStorage.ITEM);
		if (storage != null) {
			for (net.fabricmc.fabric.api.transfer.v1.storage.StorageView<FluidVariant> view : storage) {
				if (!view.isResourceBlank() && view.getAmount() > 0) {
					return FluidStackHooksFabric.fromFabric(view.getResource(), view.getAmount());
				}
			}
		}
		return FluidStack.empty();
	}
}

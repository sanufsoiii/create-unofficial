package com.simibubi.create.api.equipment.goggles;

import java.util.List;

import com.simibubi.create.foundation.utility.CreateLang;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.createmod.catnip.lang.LangBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public non-sealed interface IHaveGoggleInformation extends IHaveCustomOverlayIcon {
	String spacing = "    ";

	default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}

	default boolean containedFluidTooltip(List<Component> tooltip, boolean isPlayerSneaking, Storage<FluidVariant> handler) {
		if (handler == null)
			return false;
		LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
		CreateLang.translate("gui.goggles.fluid_container")
				.forGoggles(tooltip);

		boolean isEmpty = true;
		int tanks = 0;
		long firstCapacity = -1;

		for (StorageView<FluidVariant> view : handler) {
			if (tanks == 0)
				firstCapacity = view.getCapacity();
			tanks++;
			FluidStack fluidStack = new FluidStack(view.getResource(), view.getAmount());
			if (fluidStack.isEmpty())
				continue;

			CreateLang.fluidName(dev.architectury.fluid.FluidStack.create(fluidStack.getFluid(), fluidStack.getAmount() / 81))
					.style(ChatFormatting.GRAY)
					.forGoggles(tooltip, 1);

			CreateLang.builder()
					.add(CreateLang.number(fluidStack.getAmount() / 81)
							.add(mb)
							.style(ChatFormatting.GOLD))
					.text(ChatFormatting.GRAY, " / ")
					.add(CreateLang.number(view.getCapacity() / 81)
							.add(mb)
							.style(ChatFormatting.DARK_GRAY))
					.forGoggles(tooltip, 1);

			isEmpty = false;
		}

		if (tanks > 1) {
			if (isEmpty)
				tooltip.remove(tooltip.size() - 1);
			return true;
		}

		if (!isEmpty)
			return true;

		CreateLang.translate("gui.goggles.fluid_container.capacity")
				.add(CreateLang.number(firstCapacity / 81)
						.add(mb)
						.style(ChatFormatting.GOLD))
				.style(ChatFormatting.GRAY)
				.forGoggles(tooltip, 1);

		return true;
	}
}

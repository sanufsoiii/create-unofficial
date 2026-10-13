package com.simibubi.create.api.equipment.goggles;

import java.util.List;
import net.minecraft.network.chat.Component;

public non-sealed interface IHaveGoggleInformation extends IHaveCustomOverlayIcon {
	default boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		return false;
	}
}

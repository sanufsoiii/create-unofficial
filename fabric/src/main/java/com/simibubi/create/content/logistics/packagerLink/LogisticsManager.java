package com.simibubi.create.content.logistics.packagerLink;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;

import net.minecraft.world.item.ItemStack;

public class LogisticsManager {

	public static InventorySummary getSummaryOfNetwork(UUID freqId, boolean accurate) {
		return InventorySummary.EMPTY;
	}

	public static int getStockOf(UUID freqId, ItemStack stack, @Nullable Object ignoredHandler) {
		return 0;
	}

	public static boolean broadcastPackageRequest(UUID freqId, RequestType type, PackageOrderWithCrafts order,
												  @Nullable Object ignoredHandler, String address) {
		return true;
	}

}

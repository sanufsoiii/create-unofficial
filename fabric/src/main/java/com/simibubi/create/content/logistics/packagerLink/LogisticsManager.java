package com.simibubi.create.content.logistics.packagerLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import com.google.common.cache.Cache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.foundation.utility.TickBasedCache;

import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.ItemStack;

public class LogisticsManager {

	private static Random r = new Random();

	public static final Cache<UUID, InventorySummary> ACCURATE_SUMMARIES = new TickBasedCache<>(1, false);
	public static final Cache<UUID, InventorySummary> SUMMARIES = new TickBasedCache<>(20, false);

	public static InventorySummary getSummaryOfNetwork(UUID freqId, boolean accurate) {
		try {
			Cache<UUID, InventorySummary> cacheToUse = accurate ? LogisticsManager.ACCURATE_SUMMARIES
				: LogisticsManager.SUMMARIES;
			return cacheToUse.get(freqId, () -> createSummaryOfNetwork(freqId));
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return InventorySummary.EMPTY;
	}

	private static InventorySummary createSummaryOfNetwork(UUID freqId) {
		InventorySummary summaryOfLinks = new InventorySummary();
		for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false)) {
			InventorySummary summary = link.getSummary(null);
			if (summary != InventorySummary.EMPTY) {
				summaryOfLinks.contributingLinks++;
				summaryOfLinks.add(summary);
			}
		}
		return summaryOfLinks;
	}

	public static int getStockOf(UUID freqId, ItemStack stack, @Nullable Object ignoredHandler) {
		int sum = 0;
		for (LogisticallyLinkedBehaviour link : LogisticallyLinkedBehaviour.getAllPresent(freqId, false))
			sum += link.getSummary(null)
				.getCountOf(stack);
		return sum;
	}

	public static boolean broadcastPackageRequest(UUID freqId, RequestType type, PackageOrderWithCrafts order,
												  @Nullable Object ignoredHandler, String address) {
		if (order.isEmpty())
			return false;

		Multimap<PackagerBlockEntity, PackagingRequest> requests = findPackagersForRequest(freqId, order,
			ignoredHandler, address);

		for (PackagerBlockEntity packager : requests.keySet())
			if (packager.isTooBusyFor(type))
				return false;

		performPackageRequests(requests);
		return true;
	}

	public static Multimap<PackagerBlockEntity, PackagingRequest> findPackagersForRequest(UUID freqId,
		PackageOrderWithCrafts order, @Nullable Object ignoredHandler, String address) {
		List<BigItemStack> stacks = new ArrayList<>();

		for (BigItemStack stack : order.stacks())
			if (!stack.stack.isEmpty() && stack.count > 0)
				stacks.add(stack);

		Multimap<PackagerBlockEntity, PackagingRequest> requests = HashMultimap.create();
		Iterable<LogisticallyLinkedBehaviour> allAvailableLinks = LogisticallyLinkedBehaviour.getAllPresent(freqId, true);
		List<LogisticallyLinkedBehaviour> availableLinks = new ArrayList<>();
		for (LogisticallyLinkedBehaviour link : allAvailableLinks) {
			availableLinks.add(link);
		}

		List<LogisticallyLinkedBehaviour> usedLinks = new ArrayList<>();
		MutableBoolean finalLinkTracker = new MutableBoolean(false);
		PackageOrderWithCrafts context = order;
		int orderId = r.nextInt();

		for (int i = 0; i < stacks.size(); i++) {
			BigItemStack entry = stacks.get(i);
			int remainingCount = entry.count;
			boolean finalEntry = i == stacks.size() - 1;
			ItemStack requestedItem = entry.stack;

			for (LogisticallyLinkedBehaviour link : availableLinks) {
				int usedIndex = usedLinks.indexOf(link);
				int linkIndex = usedIndex == -1 ? usedLinks.size() : usedIndex;
				MutableBoolean isFinalLink = new MutableBoolean(false);
				if (linkIndex == usedLinks.size() - 1)
					isFinalLink = finalLinkTracker;

				Pair<PackagerBlockEntity, PackagingRequest> request = link.processRequest(requestedItem, remainingCount,
					address, linkIndex, isFinalLink, orderId, context, null);
				if (request == null)
					continue;

				requests.put(request.getFirst(), request.getSecond());

				int processedCount = request.getSecond()
					.getCount();
				if (processedCount > 0 && usedIndex == -1) {
					context = null;
					usedLinks.add(link);
					finalLinkTracker = isFinalLink;
				}

				remainingCount -= processedCount;
				if (remainingCount > 0)
					continue;
				if (finalEntry)
					finalLinkTracker.setTrue();
				break;
			}
		}
		return requests;
	}

	public static void performPackageRequests(Multimap<PackagerBlockEntity, PackagingRequest> requests) {
		Map<PackagerBlockEntity, Collection<PackagingRequest>> asMap = requests.asMap();
		for (Entry<PackagerBlockEntity, Collection<PackagingRequest>> entry : asMap.entrySet()) {
			ArrayList<PackagingRequest> queuedRequests = new ArrayList<>(entry.getValue());
			PackagerBlockEntity packager = entry.getKey();

			if (!queuedRequests.isEmpty())
				packager.flashLink();
			for (int i = 0; i < 100; i++) {
				if (queuedRequests.isEmpty())
					break;
				packager.attemptToSend(queuedRequests);
			}

			packager.triggerStockCheck();
			packager.notifyUpdate();
		}
	}

}

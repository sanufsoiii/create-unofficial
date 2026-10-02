package com.simibubi.create.content.logistics.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItemStack.ListFilterItemStack;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class ListFilterItem extends FilterItem {
	protected ListFilterItem(Properties properties) {
		super(properties);
	}

	@Override
	public List<Component> makeSummary(ItemStack filter) {
		List<Component> list = new ArrayList<>();

		ItemContainerContents contents = filter.getOrDefault(AllDataComponents.FILTER_ITEMS, ItemContainerContents.EMPTY);
		boolean blacklist = filter.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, false);

		list.add((blacklist ? CreateLang.translateDirect("gui.filter.deny_list")
			: CreateLang.translateDirect("gui.filter.allow_list")).withStyle(ChatFormatting.GOLD));
		int count = 0;
		for (ItemStack filterStack : contents.stream().toList()) {
			if (count > 3) {
				list.add(Component.literal("- ...")
					.withStyle(ChatFormatting.DARK_GRAY));
				break;
			}

			if (filterStack.isEmpty())
				continue;
			list.add(Component.literal("- ")
				.append(filterStack.getHoverName())
				.withStyle(ChatFormatting.GRAY));
			count++;
		}

		if (count == 0)
			return Collections.emptyList();

		return list;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return FilterMenuFactory.createFilterMenu(id, inv, player);
	}

	@Override
	public DataComponentType<?> getComponentType() {
		return AllDataComponents.FILTER_ITEMS;
	}

	@Override
	public FilterItemStack makeStackWrapper(ItemStack filter) {
		return new ListFilterItemStack(filter);
	}

	@Override
	public ItemStack[] getFilterItems(ItemStack stack) {
		if (stack.getOrDefault(AllDataComponents.FILTER_ITEMS_BLACKLIST, false))
			return new ItemStack[0];
		ItemContainerContents contents = stack.getOrDefault(AllDataComponents.FILTER_ITEMS, ItemContainerContents.EMPTY);
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack s : contents.stream().toList()) {
			if (!s.isEmpty())
				list.add(s);
		}
		return list.toArray(ItemStack[]::new);
	}
}

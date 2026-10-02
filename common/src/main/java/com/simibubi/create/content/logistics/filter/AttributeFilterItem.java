package com.simibubi.create.content.logistics.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.filter.FilterItemStack.AttributeFilterItemStack;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute.ItemAttributeEntry;
import com.simibubi.create.foundation.utility.CreateLang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class AttributeFilterItem extends FilterItem {
	protected AttributeFilterItem(Properties properties) {
		super(properties);
	}

	@Override
	public List<Component> makeSummary(ItemStack filter) {
		List<Component> list = new ArrayList<>();

		AttributeFilterWhitelistMode whitelistMode = filter.get(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE);
		list.add((whitelistMode == AttributeFilterWhitelistMode.WHITELIST_CONJ
			? CreateLang.translateDirect("gui.attribute_filter.allow_list_conjunctive")
			: whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ
			? CreateLang.translateDirect("gui.attribute_filter.allow_list_disjunctive")
			: CreateLang.translateDirect("gui.attribute_filter.deny_list")).withStyle(ChatFormatting.GOLD));

		int count = 0;
		List<ItemAttribute.ItemAttributeEntry> attributes = filter.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());
		for (ItemAttributeEntry attributeEntry : attributes) {
			ItemAttribute attribute = attributeEntry.attribute();
			if (attribute == null)
				continue;
			boolean inverted = attributeEntry.inverted();
			if (count > 3) {
				list.add(Component.literal("- ...")
					.withStyle(ChatFormatting.DARK_GRAY));
				break;
			}
			list.add(Component.literal("- ")
				.append(attribute.format(inverted)));
			count++;
		}

		if (count == 0)
			return Collections.emptyList();

		return list;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return FilterMenuFactory.createAttributeFilterMenu(id, inv, player);
	}

	@Override
	public DataComponentType<?> getComponentType() {
		return AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES;
	}

	@Override
	public FilterItemStack makeStackWrapper(ItemStack filter) {
		return new AttributeFilterItemStack(filter);
	}

	@Override
	public ItemStack[] getFilterItems(ItemStack stack) {
		AttributeFilterWhitelistMode whitelistMode = stack.get(AllDataComponents.ATTRIBUTE_FILTER_WHITELIST_MODE);
		List<ItemAttribute.ItemAttributeEntry> attributes = stack.getOrDefault(AllDataComponents.ATTRIBUTE_FILTER_MATCHED_ATTRIBUTES, List.of());

		if (whitelistMode == AttributeFilterWhitelistMode.WHITELIST_DISJ && attributes.size() == 1) {
			ItemAttribute attribute = attributes.getFirst().attribute();
			if (attribute != null) {
				ItemStack[] matching = attribute.getMatchingItemStacks();
				if (matching != null)
					return matching;
			}
		}
		return new ItemStack[0];
	}
}

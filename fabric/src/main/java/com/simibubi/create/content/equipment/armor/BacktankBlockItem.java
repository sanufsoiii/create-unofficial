package com.simibubi.create.content.equipment.armor;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BacktankBlockItem extends BlockItem {
	private final Supplier<Item> actualItem;

	public BacktankBlockItem(Block block, Supplier<Item> actualItem, Properties properties) {
		super(block, properties);
		this.actualItem = actualItem;
	}

	public BacktankBlockItem(Block block, Properties properties) {
		this(block, null, properties);
	}

	@Override
	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	public Item getActualItem() {
		return actualItem != null ? actualItem.get() : this;
	}
}

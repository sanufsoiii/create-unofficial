package com.simibubi.create.content.trains.track;

import com.simibubi.create.content.trains.graph.EdgePointType;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TrackTargetingBlockItem extends BlockItem {

	private EdgePointType<?> type;

	public static <T extends Block> NonNullBiFunction<? super T, Item.Properties, TrackTargetingBlockItem> ofType(
		EdgePointType<?> type) {
		return (b, p) -> new TrackTargetingBlockItem(b, p, type);
	}

	public TrackTargetingBlockItem(Block pBlock, Properties pProperties, EdgePointType<?> type) {
		super(pBlock, pProperties);
		this.type = type;
	}

	public EdgePointType<?> getType() {
		return type;
	}
}

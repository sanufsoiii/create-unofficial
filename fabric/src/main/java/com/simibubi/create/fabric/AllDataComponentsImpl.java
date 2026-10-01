package com.simibubi.create.fabric;

import com.simibubi.create.AllDataComponents;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.network.codec.StreamCodec;

public class AllDataComponentsImpl {
	public static DataComponentType<?> registerToolboxInventory() {
		return AllDataComponents.<Unit>registerComponent(
			"toolbox_inventory",
			builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
		);
	}
}

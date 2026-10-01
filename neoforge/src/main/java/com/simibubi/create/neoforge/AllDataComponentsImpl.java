package com.simibubi.create.neoforge;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.toolbox.ToolboxInventory;

import net.minecraft.core.component.DataComponentType;

public class AllDataComponentsImpl {
	public static DataComponentType<?> registerToolboxInventory() {
		return AllDataComponents.<Object>registerComponent(
			"toolbox_inventory",
			builder -> builder.persistent(ToolboxInventory.BACKWARDS_COMPAT_CODEC.xmap(i -> (Object) i, o -> (ToolboxInventory) o))
				.networkSynchronized(ToolboxInventory.STREAM_CODEC.map(i -> (Object) i, o -> (ToolboxInventory) o))
		);
	}
}

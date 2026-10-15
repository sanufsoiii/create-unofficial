package com.simibubi.create.fabric;

import com.simibubi.create.AllBlocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class CreateClientRenderLayers {

	public static void init() {
		BlockRenderLayerMap.INSTANCE.putBlocks(
			RenderType.cutoutMipped(),
			AllBlocks.SHAFT.get(),
			AllBlocks.COGWHEEL.get(),
			AllBlocks.LARGE_COGWHEEL.get(),
			AllBlocks.WATER_WHEEL.get(),
			AllBlocks.LARGE_WATER_WHEEL.get(),
			AllBlocks.HAND_CRANK.get(),
			AllBlocks.COPPER_VALVE_HANDLE.get(),
			AllBlocks.MECHANICAL_PRESS.get(),
			AllBlocks.MECHANICAL_MIXER.get(),
			AllBlocks.BASIN.get(),
			AllBlocks.BLAZE_BURNER.get(),
			AllBlocks.CHUTE.get(),
			AllBlocks.SMART_CHUTE.get(),
			AllBlocks.ANDESITE_FUNNEL.get(),
			AllBlocks.BRASS_FUNNEL.get(),
			AllBlocks.ANDESITE_TUNNEL.get(),
			AllBlocks.BRASS_TUNNEL.get(),
			AllBlocks.ITEM_DRAIN.get(),
			AllBlocks.SPOUT.get(),
			AllBlocks.FLUID_PIPE.get(),
			AllBlocks.SMART_FLUID_PIPE.get(),
			AllBlocks.FLUID_VALVE.get(),
			AllBlocks.MECHANICAL_PUMP.get()
		);
		for (var entry : AllBlocks.DYED_VALVE_HANDLES) {
			BlockRenderLayerMap.INSTANCE.putBlock(entry.get(), RenderType.cutoutMipped());
		}
	}
}

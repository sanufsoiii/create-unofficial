package com.simibubi.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreateClient {

	public static final ClientSchematicLoader SCHEMATIC_SENDER = new ClientSchematicLoader();

	public static void initClient() {
		SuperByteBufferCache cache = SuperByteBufferCache.getInstance();
		cache.registerCompartment(CachedBuffers.PARTIAL);
		cache.registerCompartment(CachedBuffers.DIRECTIONAL_PARTIAL);
		cache.registerCompartment(KineticBlockEntityRenderer.KINETIC_BLOCK);
		cache.registerCompartment(com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer.WATER_WHEEL);

		net.createmod.ponder.foundation.PonderIndex.addPlugin(new com.simibubi.create.foundation.ponder.CreatePonderPlugin());
	}
}

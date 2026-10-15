package com.simibubi.create.fabric;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.Create;
import net.fabricmc.api.ClientModInitializer;

public class CreateClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Create.LOGGER.info("Create client initializing on Fabric!");
		AllParticleTypes.registerFactories();
		AllKeys.register();
		AllPartialModels.init();
		CreateClientRenderLayers.init();
		com.simibubi.create.CreateClient.initClient();
	}
}

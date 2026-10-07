package com.simibubi.create.fabric;

import com.simibubi.create.Create;
import net.fabricmc.api.ClientModInitializer;

public class CreateClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Create.LOGGER.info("Create client initializing on Fabric!");
	}
}

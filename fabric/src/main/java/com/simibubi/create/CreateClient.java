package com.simibubi.create;

import com.simibubi.create.content.schematics.client.ClientSchematicLoader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreateClient {

	public static final ClientSchematicLoader SCHEMATIC_SENDER = new ClientSchematicLoader();

	public static void initClient() {
		// Common client setup
	}
}

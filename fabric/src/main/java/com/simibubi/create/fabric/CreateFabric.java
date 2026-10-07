package com.simibubi.create.fabric;

import com.simibubi.create.Create;
import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.boiler.BoilerHeaterDefaults;
import com.simibubi.create.api.registry.SimpleRegistry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class CreateFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Create.LOGGER.info("{} {} initializing on Fabric/Quilt! Commit: {}", Create.NAME, CreateBuildInfo.VERSION, CreateBuildInfo.GIT_COMMIT);

		Create.init();
		com.simibubi.create.infrastructure.config.AllConfigs.register();

		// Register default passive boiler heater for Fabric
		TagKey<net.minecraft.world.level.block.Block> passiveBoilerHeatersTag =
			TagKey.create(BuiltInRegistries.BLOCK.key(), Create.asResource("passive_boiler_heaters"));
		BoilerHeater.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(passiveBoilerHeatersTag, BoilerHeater.PASSIVE));
	}
}

package com.simibubi.create.fabric;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.boiler.BoilerHeaterDefaults;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.foundation.data.CreateRegistrate;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class CreateFabric implements ModInitializer {
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Create.ID);

	@Override
	public void onInitialize() {
		Create.LOGGER.info("{} {} initializing on Fabric/Quilt! Commit: {}", Create.NAME, CreateBuildInfo.VERSION, CreateBuildInfo.GIT_COMMIT);

		Create.init();
		AllArmorMaterials.register();
		AllCreativeModeTabs.register();
		AllBlocks.register();
		AllItems.register();
		com.simibubi.create.AllBlockEntityTypes.register();
		com.simibubi.create.AllParticleTypes.register();
		com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes.init();
		com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes.init();
		REGISTRATE.register();
		com.simibubi.create.infrastructure.config.AllConfigs.register();

		// Register default passive boiler heater for Fabric
		TagKey<net.minecraft.world.level.block.Block> passiveBoilerHeatersTag =
			TagKey.create(BuiltInRegistries.BLOCK.key(), Create.asResource("passive_boiler_heaters"));
		BoilerHeater.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(passiveBoilerHeatersTag, BoilerHeater.PASSIVE));
		com.simibubi.create.content.fluids.tank.BoilerHeaters.registerDefaults();
		com.simibubi.create.api.event.PipeCollisionEvent.FLOW.register(com.simibubi.create.content.fluids.FluidReactions::handlePipeFlowCollisionFallback);
		com.simibubi.create.api.event.PipeCollisionEvent.SPILL.register(com.simibubi.create.content.fluids.FluidReactions::handlePipeSpillCollisionFallback);
		net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register(com.simibubi.create.content.kinetics.crank.ValveHandleBlock::onBlockActivated);
		net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register(com.simibubi.create.foundation.block.ItemUseOverrides::onBlockActivated);
	}
}

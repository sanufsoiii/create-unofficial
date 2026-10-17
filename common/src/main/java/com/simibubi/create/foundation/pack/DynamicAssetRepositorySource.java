package com.simibubi.create.foundation.pack;

import java.util.function.Consumer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

/**
 * Supplies the dynamically downloaded official Create ARR assets pack to Minecraft's PackRepository.
 */
public class DynamicAssetRepositorySource implements RepositorySource {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void loadPacks(Consumer<Pack> consumer) {
		if (!DynamicAssetManager.isClient()) {
			return;
		}

		try {
			Pack pack = DynamicAssetManager.createPack();
			if (pack != null) {
				consumer.accept(pack);
				LOGGER.info("[Create Assets] Successfully registered dynamic official assets pack");
			} else {
				LOGGER.warn("[Create Assets] Could not register official assets pack (createPack returned null)");
			}
		} catch (Throwable t) {
			LOGGER.error("[Create Assets] Error during loadPacks for dynamic asset repository", t);
		}
	}
}

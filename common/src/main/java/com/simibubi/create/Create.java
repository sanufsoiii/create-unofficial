package com.simibubi.create;

import java.util.Random;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;

import com.simibubi.create.content.logistics.packagerLink.GlobalLogisticsManager;
import com.simibubi.create.infrastructure.command.ServerLagger;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.resources.ResourceLocation;

public class Create {
	public static final String ID = "create";
	public static final String NAME = "Create";

	public static final Logger LOGGER = LogUtils.getLogger();

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
		.disableHtmlEscaping()
		.create();

	/**
	 * Use the {@link Random} of a local {@link Level} or {@link Entity} or create one
	 */
	@Deprecated
	public static final Random RANDOM = new Random();

	public static final ServerLagger LAGGER = new ServerLagger();
	public static final GlobalLogisticsManager LOGISTICS = new GlobalLogisticsManager();

	public static LangBuilder lang() {
		return new LangBuilder(ID);
	}

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	public static void init() {
		AllSoundEvents.register();
		AllMapDecorationTypes.register();
		com.simibubi.create.infrastructure.worldgen.AllFeatures.register();
	}
}

package com.simibubi.create.foundation.resources;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import io.github.fabricators_of_create.porting_lib_ufo.event.common.AddPackFindersCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * Discovers and registers local official Create asset archives without bundling ARR files or downloading from network.
 */
public class CreateAssetDownloader implements PreLaunchEntrypoint {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final String PACK_ID = "create:original_assets";
	private static File cachedAssetsJar = null;
	private static boolean initialized = false;
	private static boolean packFinderRegistered = false;

	public static File getCachedAssetsJar() {
		return cachedAssetsJar;
	}

	public static boolean hasAssets() {
		return cachedAssetsJar != null && cachedAssetsJar.exists() && cachedAssetsJar.length() > 0;
	}

	@Override
	public void onPreLaunch() {
		preLaunch();
	}

	public static void preLaunch() {
		init();
	}

	public static synchronized void init() {
		if (initialized)
			return;
		initialized = true;
		findAndRegisterAssets();
	}

	public static synchronized void findAndRegisterAssets() {
		if (cachedAssetsJar != null && cachedAssetsJar.exists())
			return;

		try {
			Path gameDir = FabricLoader.getInstance().getGameDir();

			// 1. Check resourcepacks folder
			Path rpDir = gameDir.resolve("resourcepacks");
			File found = scanDirectoryForCreateAssets(rpDir);

			// 2. Check mods folder for official Create jar
			if (found == null) {
				Path modsDir = gameDir.resolve("mods");
				found = scanDirectoryForCreateAssets(modsDir);
			}

			// 3. Check .cache/create folder (for backwards compatibility)
			if (found == null) {
				Path cacheDir = gameDir.resolve(".cache").resolve("create");
				found = scanDirectoryForCreateAssets(cacheDir);
			}

			if (found != null) {
				LOGGER.info("[Create Reforged] Found official Create assets archive: {}", found.getAbsolutePath());
				cachedAssetsJar = found;
				registerPackFinder();
			} else {
				LOGGER.info("[Create Reforged] No official Create assets found locally. Player will be redirected to official CurseForge page.");
			}
		} catch (Throwable t) {
			LOGGER.warn("[Create Reforged] Error scanning for official Create assets: ", t);
		}
	}

	private static File scanDirectoryForCreateAssets(Path dir) {
		if (!Files.isDirectory(dir))
			return null;

		try (var stream = Files.list(dir)) {
			var files = stream.filter(Files::isRegularFile).map(Path::toFile).toList();
			for (File file : files) {
				String name = file.getName().toLowerCase();
				if ((name.endsWith(".jar") || name.endsWith(".zip")) && isCreateAssetsArchive(file)) {
					return file;
				}
			}
		} catch (Throwable ignored) {
		}
		return null;
	}

	private static boolean isCreateAssetsArchive(File file) {
		if (!file.exists() || file.length() < 1000)
			return false;

		Path myModPath = FabricLoader.getInstance().getModContainer("create")
				.flatMap(c -> c.getOrigin().getPaths().stream().findFirst())
				.orElse(null);
		if (myModPath != null) {
			try {
				if (Files.isSameFile(file.toPath(), myModPath))
					return false;
			} catch (Exception ignored) {
			}
		}

		try (ZipFile zip = new ZipFile(file)) {
			ZipEntry entry1 = zip.getEntry("assets/create/textures/block/cogwheel.png");
			ZipEntry entry2 = zip.getEntry("assets/create/models/block/cogwheel.json");
			return entry1 != null || entry2 != null;
		} catch (Exception ignored) {
			return false;
		}
	}

	private static void registerPackFinder() {
		if (packFinderRegistered || cachedAssetsJar == null || !cachedAssetsJar.exists())
			return;
		packFinderRegistered = true;

		AddPackFindersCallback.EVENT.register(sources -> {
			sources.accept(packConsumer -> {
				PackLocationInfo locationInfo = new PackLocationInfo(
						PACK_ID,
						Component.literal("Create Original Assets"),
						PackSource.BUILT_IN,
						Optional.empty()
				);

				Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier() {
					@Override
					public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo info) {
						return new FilePackResources(info, new SharedZipFileAccess(cachedAssetsJar), "");
					}

					@Override
					public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
						return new FilePackResources(info, new SharedZipFileAccess(cachedAssetsJar), "");
					}
				};

				Pack.Metadata metadata = new Pack.Metadata(
						Component.literal("Official assets loaded from " + cachedAssetsJar.getName()),
						PackCompatibility.COMPATIBLE,
						FeatureFlagSet.of(),
						java.util.Collections.emptyList()
				);

				PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, true);

				Pack pack = new Pack(locationInfo, resourcesSupplier, metadata, selectionConfig);
				packConsumer.accept(pack);
			});
		});
	}
}

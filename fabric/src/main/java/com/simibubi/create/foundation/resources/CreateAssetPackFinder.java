package com.simibubi.create.foundation.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import io.github.fabricators_of_create.porting_lib_ufo.event.common.AddPackFindersCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FilePackResources.SharedZipFileAccess;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * Discovers and registers local official Create asset archives without bundling ARR files or downloading from network.
 */
public class CreateAssetPackFinder implements PreLaunchEntrypoint {
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

	private static class FilteredAssetPackResources extends FilePackResources {
		private static final Map<String, String> FALLBACK_EN = Map.ofEntries(
			Map.entry("create.ponder.hold_to_ponder", "Hold [%1$s] to Ponder"),
			Map.entry("create.ponder.subject", "Subject of this scene"),
			Map.entry("create.ponder.pondering", "Pondering about..."),
			Map.entry("create.ponder.identify_mode", "Identify mode active.\nUnpause with [%1$s]"),
			Map.entry("create.ponder.associated", "Associated Entries"),
			Map.entry("create.ponder.close", "Close"),
			Map.entry("create.ponder.identify", "Identify"),
			Map.entry("create.ponder.next", "Next Scene"),
			Map.entry("create.ponder.next_up", "Up Next:"),
			Map.entry("create.ponder.previous", "Previous Scene"),
			Map.entry("create.ponder.replay", "Replay"),
			Map.entry("create.ponder.think_back", "Think Back"),
			Map.entry("create.ponder.slow_text", "Comfy Reading"),
			Map.entry("create.ponder.exit", "Exit"),
			Map.entry("create.ponder.welcome", "Welcome to Ponder"),
			Map.entry("create.ponder.categories", "Available Categories in Create"),
			Map.entry("create.ponder.index_description", "Click one of the icons below to learn about its associated Items and Blocks"),
			Map.entry("create.ponder.index_title", "Ponder Index")
		);

		private static final Map<String, String> FALLBACK_RU = Map.ofEntries(
			Map.entry("create.ponder.hold_to_ponder", "Удерживайте [%1$s] для размышления"),
			Map.entry("create.ponder.subject", "Тема этой сцены"),
			Map.entry("create.ponder.pondering", "Размышляем над..."),
			Map.entry("create.ponder.identify_mode", "Режим идентификации включён.\nСнять паузу: [%1$s]"),
			Map.entry("create.ponder.associated", "Связанные сцены"),
			Map.entry("create.ponder.close", "Закрыть"),
			Map.entry("create.ponder.identify", "Определить"),
			Map.entry("create.ponder.next", "Следующая сцена"),
			Map.entry("create.ponder.next_up", "Размышлять дальше над:"),
			Map.entry("create.ponder.previous", "Предыдущая сцена"),
			Map.entry("create.ponder.replay", "Повторить сцену"),
			Map.entry("create.ponder.think_back", "Назад"),
			Map.entry("create.ponder.slow_text", "Удобное чтение"),
			Map.entry("create.ponder.exit", "Выйти"),
			Map.entry("create.ponder.welcome", "Добро пожаловать в размышления!"),
			Map.entry("create.ponder.categories", "Категории, доступные в Create"),
			Map.entry("create.ponder.index_description", "Кликните по одной из иконок, чтобы узнать о связанных с ней предметах и блоках"),
			Map.entry("create.ponder.index_title", "Содержание размышлений")
		);

		private static final Map<String, byte[]> PONDER_FILE_CACHE = new HashMap<>();
		private static boolean ponderCacheLoaded = false;

		private static synchronized void ensurePonderCacheLoaded() {
			if (ponderCacheLoaded)
				return;
			ponderCacheLoaded = true;
			if (cachedAssetsJar == null || !cachedAssetsJar.exists())
				return;

			try (ZipFile zip = new ZipFile(cachedAssetsJar)) {
				ZipEntry ponderEntry = null;
				var entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry e = entries.nextElement();
					if (e.getName().startsWith("META-INF/jarjar/ponder-") && e.getName().endsWith(".jar")) {
						ponderEntry = e;
						break;
					}
				}
				if (ponderEntry == null)
					return;

				try (InputStream jis = zip.getInputStream(ponderEntry);
					 ZipInputStream subZip = new ZipInputStream(jis)) {
					ZipEntry entry;
					while ((entry = subZip.getNextEntry()) != null) {
						if (!entry.isDirectory()) {
							byte[] bytes = subZip.readAllBytes();
							PONDER_FILE_CACHE.put(entry.getName(), bytes);
						}
					}
				}
				LOGGER.info("[Create Reforged] Loaded {} resources from embedded ponder jar", PONDER_FILE_CACHE.size());
			} catch (Throwable t) {
				LOGGER.warn("[Create Reforged] Error reading embedded ponder jar: ", t);
			}
		}

		public FilteredAssetPackResources(PackLocationInfo info, SharedZipFileAccess zip, String prefix) {
			super(info, zip, prefix);
			ensurePonderCacheLoaded();
		}

		@Override
		public Set<String> getNamespaces(PackType type) {
			if (type == PackType.SERVER_DATA) {
				return Collections.emptySet();
			}
			Set<String> set = new HashSet<>(super.getNamespaces(type));
			set.add("ponder");
			return set;
		}

		@Override
		public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
			if (type == PackType.SERVER_DATA) {
				return null;
			}

			// 1. If requesting a ponder namespace resource from embedded ponder jar
			if (location.getNamespace().equals("ponder")) {
				String path = "assets/ponder/" + location.getPath();
				byte[] data = PONDER_FILE_CACHE.get(path);
				if (data != null) {
					return () -> new ByteArrayInputStream(data);
				}
			}

			// 2. If requesting create lang files, inject ponder UI translations
			if (location.getNamespace().equals("create") && location.getPath().startsWith("lang/") && location.getPath().endsWith(".json")) {
				IoSupplier<InputStream> originalSupplier = super.getResource(type, location);
				if (originalSupplier == null)
					return null;

				return () -> {
					InputStream in = originalSupplier.get();
					try {
						JsonObject json = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
						String langFile = location.getPath().substring("lang/".length());

						// Check if ponder jar has this lang file
						byte[] ponderLangBytes = PONDER_FILE_CACHE.get("assets/ponder/lang/" + langFile);
						if (ponderLangBytes != null) {
							try {
								JsonObject ponderJson = JsonParser.parseString(new String(ponderLangBytes, StandardCharsets.UTF_8)).getAsJsonObject();
								for (var entry : ponderJson.entrySet()) {
									String k = entry.getKey();
									String v = entry.getValue().getAsString();
									if (k.startsWith("ponder.ui.")) {
										String legacyKey = "create.ponder." + k.substring("ponder.ui.".length());
										if (!json.has(legacyKey)) json.addProperty(legacyKey, v);
									}
									if (!json.has(k)) json.addProperty(k, v);
								}
							} catch (Exception ignored) {
							}
						}

						// Fallback defaults if still missing
						Map<String, String> fallbacks = langFile.startsWith("ru_") ? FALLBACK_RU : FALLBACK_EN;
						for (var entry : fallbacks.entrySet()) {
							if (!json.has(entry.getKey())) {
								json.addProperty(entry.getKey(), entry.getValue());
							}
						}

						return new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
					} catch (Exception e) {
						LOGGER.warn("[Create Reforged] Error enriching language file {}: ", location, e);
						return originalSupplier.get();
					}
				};
			}

			return super.getResource(type, location);
		}
	}

	private static void registerPackFinder() {
		if (packFinderRegistered || cachedAssetsJar == null || !cachedAssetsJar.exists())
			return;
		packFinderRegistered = true;

		// Only register pack finder on client side since dedicated server does not need client textures/models
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			return;

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
						return new FilteredAssetPackResources(info, new SharedZipFileAccess(cachedAssetsJar), "");
					}

					@Override
					public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
						return new FilteredAssetPackResources(info, new SharedZipFileAccess(cachedAssetsJar), "");
					}
				};

				Pack.Metadata metadata = new Pack.Metadata(
						Component.literal("Official assets loaded from " + cachedAssetsJar.getName()),
						PackCompatibility.COMPATIBLE,
						FeatureFlagSet.of(),
						Collections.emptyList()
				);

				PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, true);

				Pack pack = new Pack(locationInfo, resourcesSupplier, metadata, selectionConfig);
				packConsumer.accept(pack);
			});
		});
	}
}

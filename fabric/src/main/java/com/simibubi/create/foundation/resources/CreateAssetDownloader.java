package com.simibubi.create.foundation.resources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

public class CreateAssetDownloader implements PreLaunchEntrypoint {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final String PACK_ID = "create:original_assets";
	private static final String DOWNLOAD_URL = "https://cdn.modrinth.com/data/LNytGWDc/versions/UjX6dr61/create-1.21.1-6.0.10.jar";
	private static final String EXPECTED_SHA512 = "11cc8fc049d2f67f6548c7abfada6b82a3adb5c7ca410a742de04bbca76e03862c518721b88d806f6e6d768a4d68531fdb903a85859b25d1484d550cc7bafd4b";
	private static final String JAR_FILE_NAME = "create-1.21.1-6.0.10.jar";
	private static final String TEXTURES_ZIP_NAME = "create-textures.zip";

	private static File cachedTexturesZip = null;
	private static boolean initialized = false;

	public static File getCachedTexturesZip() {
		return cachedTexturesZip;
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
		try {
			Path cacheDir = FabricLoader.getInstance().getGameDir().resolve(".cache").resolve("create");
			Files.createDirectories(cacheDir);
			File jarFile = cacheDir.resolve(JAR_FILE_NAME).toFile();
			File texturesZip = cacheDir.resolve(TEXTURES_ZIP_NAME).toFile();

			if (!isValidFile(jarFile)) {
				LOGGER.info("[Create] Original Create assets not found or corrupted in cache. Downloading from {}...", DOWNLOAD_URL);
				downloadFile(DOWNLOAD_URL, jarFile);
				if (!isValidFile(jarFile)) {
					LOGGER.error("[Create] Downloaded original assets file failed checksum verification!");
					return;
				}
				LOGGER.info("[Create] Successfully downloaded original assets jar ({} bytes).", jarFile.length());
				// Invalidate textures zip if jar was redownloaded
				if (texturesZip.exists()) {
					texturesZip.delete();
				}
			} else {
				LOGGER.info("[Create] Original Create assets found in cache: {}", jarFile.getAbsolutePath());
			}

			// Generate textures-only zip if missing or empty
			if (!texturesZip.exists() || texturesZip.length() == 0) {
				LOGGER.info("[Create] Preparing textures-only pack from original jar...");
				extractTexturesZip(jarFile, texturesZip);
				LOGGER.info("[Create] Textures pack ready ({} bytes).", texturesZip.length());
			}

			cachedTexturesZip = texturesZip;
			registerPackFinder();
		} catch (Throwable t) {
			LOGGER.error("[Create] Failed to initialize Create original assets downloader/pack: ", t);
		}
	}

	private static void extractTexturesZip(File originalJar, File destinationZip) throws Exception {
		File tempZip = new File(destinationZip.getParentFile(), destinationZip.getName() + ".tmp");
		if (tempZip.exists())
			tempZip.delete();

		try (ZipFile zipIn = new ZipFile(originalJar);
			 ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(tempZip))) {

			var entries = zipIn.entries();
			byte[] buffer = new byte[16384];

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();

				if (name.startsWith("assets/create/textures/") || name.equals("pack.mcmeta")) {
					ZipEntry newEntry = new ZipEntry(name);
					newEntry.setTime(entry.getTime());
					zipOut.putNextEntry(newEntry);

					try (InputStream is = zipIn.getInputStream(entry)) {
						int len;
						while ((len = is.read(buffer)) > 0) {
							zipOut.write(buffer, 0, len);
						}
					}
					zipOut.closeEntry();
				}
			}
		}

		if (destinationZip.exists())
			destinationZip.delete();
		if (!tempZip.renameTo(destinationZip)) {
			Files.move(tempZip.toPath(), destinationZip.toPath());
		}
	}

	private static void registerPackFinder() {
		if (cachedTexturesZip == null || !cachedTexturesZip.exists())
			return;

		AddPackFindersCallback.EVENT.register(sources -> {
			sources.accept(packConsumer -> {
				PackLocationInfo locationInfo = new PackLocationInfo(
						PACK_ID,
						Component.literal("Create Textures (Original)"),
						PackSource.BUILT_IN,
						Optional.empty()
				);

				Pack.ResourcesSupplier resourcesSupplier = new Pack.ResourcesSupplier() {
					@Override
					public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo info) {
						return new FilePackResources(info, new SharedZipFileAccess(cachedTexturesZip), "");
					}

					@Override
					public net.minecraft.server.packs.PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
						return new FilePackResources(info, new SharedZipFileAccess(cachedTexturesZip), "");
					}
				};

				Pack.Metadata metadata = new Pack.Metadata(
						Component.literal("Original textures from Create"),
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

	private static boolean isValidFile(File file) {
		if (!file.exists() || file.length() == 0)
			return false;

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			try (InputStream is = Files.newInputStream(file.toPath())) {
				byte[] buf = new byte[8192];
				int r;
				while ((r = is.read(buf)) != -1) {
					digest.update(buf, 0, r);
				}
			}
			byte[] hashBytes = digest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString().equalsIgnoreCase(EXPECTED_SHA512);
		} catch (Exception e) {
			LOGGER.warn("[Create] Failed to verify checksum for {}: {}", file.getName(), e.getMessage());
			return false;
		}
	}

	private static void downloadFile(String urlString, File destination) throws Exception {
		File tempFile = new File(destination.getParentFile(), destination.getName() + ".tmp");
		if (tempFile.exists())
			tempFile.delete();

		URI uri = new URI(urlString);
		HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
		conn.setRequestProperty("User-Agent", "Create-Fabric-Downloader/1.0");
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);
		conn.setInstanceFollowRedirects(true);

		int responseCode = conn.getResponseCode();
		if (responseCode >= 300 && responseCode < 400) {
			String newUrl = conn.getHeaderField("Location");
			if (newUrl != null) {
				conn = (HttpURLConnection) new URI(newUrl).toURL().openConnection();
				conn.setRequestProperty("User-Agent", "Create-Fabric-Downloader/1.0");
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(30000);
			}
		}

		try (InputStream in = new BufferedInputStream(conn.getInputStream());
			 FileOutputStream out = new FileOutputStream(tempFile)) {
			byte[] buf = new byte[16384];
			int bytesRead;
			while ((bytesRead = in.read(buf)) != -1) {
				out.write(buf, 0, bytesRead);
			}
		}

		if (destination.exists())
			destination.delete();
		if (!tempFile.renameTo(destination)) {
			Files.move(tempFile.toPath(), destination.toPath());
		}
	}
}

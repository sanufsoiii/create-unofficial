package com.simibubi.create.foundation.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

/**
 * Manages the dynamic download and mounting of official Create ARR assets at runtime.
 * Allows the mod jar to be distributed without bundled copyrighted assets under MIT.
 */
public class DynamicAssetManager {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static final String OFFICIAL_CREATE_VERSION = "6.0.10";
	public static final String MODRINTH_JAR_URL =
		"https://cdn.modrinth.com/data/LNytGWDc/versions/UjX6dr61/create-1.21.1-6.0.10.jar";
	public static final String EXPECTED_SHA1 = "0e97e49837bed766e6f28a4c95b04885d6acc353";

	public static final String PACK_ID = "create:official_assets";
	public static final String PACK_TITLE = "Create " + OFFICIAL_CREATE_VERSION + " Official Assets";
	public static final String ASSET_ZIP_NAME = "create_official_assets_" + OFFICIAL_CREATE_VERSION + ".zip";

	private static final Object LOCK = new Object();
	private static volatile Path cachedAssetZipPath = null;
	private static final AtomicBoolean downloadStarted = new AtomicBoolean(false);

	public static boolean isClient() {
		try {
			return Platform.getEnv() == EnvType.CLIENT;
		} catch (Throwable t) {
			return true;
		}
	}

	public static Path getCacheDir() {
		return Platform.getGameFolder().resolve("assets_cache");
	}

	public static Path getAssetZipPath() {
		return getCacheDir().resolve(ASSET_ZIP_NAME);
	}

	/**
	 * Asynchronously pre-initiates the download on game startup.
	 */
	public static void preInit() {
		if (!isClient())
			return;

		if (downloadStarted.compareAndSet(false, true)) {
			CompletableFuture.runAsync(() -> {
				try {
					ensureAssetsExist();
				} catch (Throwable t) {
					LOGGER.error("[Create Assets] Pre-init asset download failed", t);
				}
			});
		}
	}

	/**
	 * Ensures the assets zip file exists and is valid. If not, downloads and extracts it.
	 */
	public static Path ensureAssetsExist() {
		if (cachedAssetZipPath != null && Files.exists(cachedAssetZipPath)) {
			return cachedAssetZipPath;
		}

		synchronized (LOCK) {
			if (cachedAssetZipPath != null && Files.exists(cachedAssetZipPath)) {
				return cachedAssetZipPath;
			}

			Path zipPath = getAssetZipPath();
			if (isValidAssetZip(zipPath)) {
				cachedAssetZipPath = zipPath;
				return cachedAssetZipPath;
			}

			try {
				Files.createDirectories(getCacheDir());

				// Check if there is already an official jar placed locally in assets_cache or mods
				Path localJar = findLocalOfficialJar();
				if (localJar != null) {
					LOGGER.info("[Create Assets] Found local official Create jar at {}. Extracting assets...", localJar);
					extractAssetsFromJar(localJar, zipPath);
				} else {
					LOGGER.info("[Create Assets] Official assets not found. Downloading Create {} from Modrinth CDN...",
						OFFICIAL_CREATE_VERSION);
					Path downloadedJar = downloadOfficialJar();
					try {
						extractAssetsFromJar(downloadedJar, zipPath);
					} finally {
						Files.deleteIfExists(downloadedJar);
					}
				}

				if (isValidAssetZip(zipPath)) {
					LOGGER.info("[Create Assets] Successfully prepared and verified {}", zipPath);
					cachedAssetZipPath = zipPath;
					return cachedAssetZipPath;
				} else {
					throw new IOException("Created asset zip is invalid or incomplete: " + zipPath);
				}
			} catch (Exception e) {
				LOGGER.error("[Create Assets] Failed to download/extract official Create assets!", e);
				return null;
			}
		}
	}

	private static boolean isValidAssetZip(Path path) {
		if (path == null || !Files.exists(path) || !Files.isRegularFile(path))
			return false;

		try {
			if (Files.size(path) < 100_000)
				return false;

			try (ZipFile zip = new ZipFile(path.toFile())) {
				ZipEntry mcmeta = zip.getEntry("pack.mcmeta");
				ZipEntry testTexture = zip.getEntry("assets/create/textures/block/brass_casing.png");
				return mcmeta != null && testTexture != null;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static Path findLocalOfficialJar() {
		Path cacheDir = getCacheDir();
		Path candidateInCache = cacheDir.resolve("create-1.21.1-" + OFFICIAL_CREATE_VERSION + ".jar");
		if (Files.exists(candidateInCache))
			return candidateInCache;

		Path modsDir = Platform.getGameFolder().resolve("mods");
		if (Files.exists(modsDir)) {
			Path candidateInMods = modsDir.resolve("create-1.21.1-" + OFFICIAL_CREATE_VERSION + ".jar");
			if (Files.exists(candidateInMods))
				return candidateInMods;
		}

		return null;
	}

	private static Path downloadOfficialJar() throws IOException {
		Path tempFile = getCacheDir().resolve("create-" + OFFICIAL_CREATE_VERSION + ".jar.download");
		Files.deleteIfExists(tempFile);

		URL url = URI.create(MODRINTH_JAR_URL).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Create-Architectury-Asset-Downloader/" + OFFICIAL_CREATE_VERSION);
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(30000);
		conn.setInstanceFollowRedirects(true);

		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			throw new IOException("Failed to download official jar: HTTP " + status + " " + conn.getResponseMessage());
		}

		long contentLength = conn.getContentLengthLong();
		LOGGER.info("[Create Assets] Connecting to CDN... Size: {} MB",
			contentLength > 0 ? String.format("%.2f", contentLength / (1024.0 * 1024.0)) : "unknown");

		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			throw new RuntimeException("SHA-1 algorithm not available", e);
		}

		try (InputStream in = new BufferedInputStream(conn.getInputStream());
			 BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(tempFile))) {
			byte[] buffer = new byte[8192];
			long downloaded = 0;
			long lastLogTime = System.currentTimeMillis();
			int bytesRead;

			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				sha1.update(buffer, 0, bytesRead);
				downloaded += bytesRead;

				long now = System.currentTimeMillis();
				if (now - lastLogTime > 2000) {
					if (contentLength > 0) {
						int pct = (int) ((downloaded * 100) / contentLength);
						LOGGER.info("[Create Assets] Downloading: {}% ({}/{} MB)", pct,
							String.format("%.1f", downloaded / (1024.0 * 1024.0)),
							String.format("%.1f", contentLength / (1024.0 * 1024.0)));
					} else {
						LOGGER.info("[Create Assets] Downloaded {} MB",
							String.format("%.1f", downloaded / (1024.0 * 1024.0)));
					}
					lastLogTime = now;
				}
			}
		}

		byte[] digest = sha1.digest();
		StringBuilder hex = new StringBuilder();
		for (byte b : digest) {
			hex.append(String.format("%02x", b));
		}
		String calculatedSha1 = hex.toString();

		if (!calculatedSha1.equalsIgnoreCase(EXPECTED_SHA1)) {
			Files.deleteIfExists(tempFile);
			throw new IOException("SHA-1 mismatch on downloaded jar! Expected: " + EXPECTED_SHA1 + ", got: " + calculatedSha1);
		}

		LOGGER.info("[Create Assets] Download completed and verified (SHA-1: {})", calculatedSha1);
		return tempFile;
	}

	private static void extractAssetsFromJar(Path sourceJar, Path targetZip) throws IOException {
		Path tempZip = targetZip.resolveSibling(targetZip.getFileName().toString() + ".tmp");
		Files.deleteIfExists(tempZip);

		try (ZipFile jarZip = new ZipFile(sourceJar.toFile());
			 ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(tempZip)))) {

			// Write pack.mcmeta with pack_format 34 for 1.21.1
			ZipEntry mcmetaEntry = new ZipEntry("pack.mcmeta");
			zos.putNextEntry(mcmetaEntry);
			String mcmetaContent = "{\n" +
				"  \"pack\": {\n" +
				"    \"description\": \"Official Create " + OFFICIAL_CREATE_VERSION + " Assets (ARR)\",\n" +
				"    \"pack_format\": 34\n" +
				"  }\n" +
				"}";
			zos.write(mcmetaContent.getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();

			int count = 0;
			Enumeration<? extends ZipEntry> entries = jarZip.entries();
			byte[] buffer = new byte[8192];

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();

				if (name.startsWith("assets/")) {
					ZipEntry newEntry = new ZipEntry(name);
					newEntry.setTime(entry.getTime());
					zos.putNextEntry(newEntry);

					if (!entry.isDirectory()) {
						try (InputStream is = jarZip.getInputStream(entry)) {
							int read;
							while ((read = is.read(buffer)) != -1) {
								zos.write(buffer, 0, read);
							}
						}
					}
					zos.closeEntry();
					count++;
				}
			}
			LOGGER.info("[Create Assets] Extracted {} asset files into {}", count, tempZip.getFileName());
		}

		Files.move(tempZip, targetZip, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Creates a Pack instance for the official assets.
	 */
	public static Pack createPack() {
		Path zip = ensureAssetsExist();
		if (zip == null || !Files.exists(zip)) {
			return null;
		}

		PackLocationInfo info = new PackLocationInfo(
			PACK_ID,
			Component.literal(PACK_TITLE),
			PackSource.BUILT_IN,
			Optional.empty()
		);

		Pack.ResourcesSupplier supplier = new FilePackResources.FileResourcesSupplier(zip);
		PackSelectionConfig selectionConfig = new PackSelectionConfig(
			true,
			Pack.Position.TOP,
			false
		);

		return Pack.readMetaAndCreate(info, supplier, PackType.CLIENT_RESOURCES, selectionConfig);
	}

	public static RepositorySource createRepositorySource() {
		return new DynamicAssetRepositorySource();
	}
}

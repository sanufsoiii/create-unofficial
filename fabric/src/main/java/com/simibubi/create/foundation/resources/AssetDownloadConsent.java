package com.simibubi.create.foundation.resources;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Stores the player's explicit decision about downloading the official Create assets.
 * <p>
 * Create's assets are All Rights Reserved, so this mod cannot bundle them and instead
 * has to fetch them from the Modrinth CDN. Nothing is allowed to touch the network
 * before the player agreed to it: the default state is {@link State#ASK}, and the
 * downloader checks {@link #isAllowed()} before doing anything.
 * <p>
 * The decision is persisted in {@code config/create-assets-consent.json} so it survives
 * restarts and can be changed (or reset by deleting the file) by the player at any time.
 */
public final class AssetDownloadConsent {
	public static final String CONFIG_FILE_NAME = "create-assets-consent.json";
	public static final String ASSETS_VERSION = "6.0.10";
	public static final String DOWNLOAD_URL =
		"https://cdn.modrinth.com/data/LNytGWDc/versions/UjX6dr61/create-1.21.1-6.0.10.jar";

	public enum State {
		/** Nothing has been decided yet: no downloads, ask the player in-game. */
		ASK,
		/** The player explicitly allowed the download. */
		ALLOWED,
		/** The player refused the download: never download, never ask again. */
		DENIED
	}

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
		.create();

	private static volatile State state;

	private AssetDownloadConsent() {
	}

	public static State getState() {
		State current = state;
		if (current == null) {
			synchronized (AssetDownloadConsent.class) {
				current = state;
				if (current == null) {
					current = readState();
					state = current;
				}
			}
		}
		return current;
	}

	public static boolean isAllowed() {
		return getState() == State.ALLOWED;
	}

	public static boolean needsPrompt() {
		return getState() == State.ASK;
	}

	public static synchronized void allow() {
		applyState(State.ALLOWED);
	}

	public static synchronized void deny() {
		applyState(State.DENIED);
	}

	public static Path configPath() {
		try {
			return FabricLoader.getInstance()
				.getGameDir()
				.resolve("config")
				.resolve(CONFIG_FILE_NAME);
		} catch (Throwable t) {
			return Path.of("config", CONFIG_FILE_NAME);
		}
	}

	private static synchronized void applyState(State newState) {
		state = newState;
		writeState(newState);
	}

	private static State readState() {
		Path path = configPath();
		if (!Files.isRegularFile(path))
			return State.ASK;

		try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			JsonObject root = JsonParser.parseReader(reader)
				.getAsJsonObject();
			if (!root.has("state"))
				return State.ASK;
			return State.valueOf(root.get("state")
				.getAsString()
				.trim()
				.toUpperCase());
		} catch (Exception e) {
			LOGGER.warn("[Create] Could not read asset download consent from {}: {}", path, e.toString());
			return State.ASK;
		}
	}

	private static void writeState(State newState) {
		Path path = configPath();
		try {
			Files.createDirectories(path.getParent());
			JsonObject root = new JsonObject();
			root.addProperty("state", newState.name());
			root.addProperty("assets_version", ASSETS_VERSION);
			root.addProperty("download_url", DOWNLOAD_URL);
			root.addProperty("decided_at", Instant.now()
				.toString());
			root.addProperty("comment", "ASK = never download and ask in-game, ALLOWED = may download official "
				+ "Create assets, DENIED = never download. Delete this file to be asked again.");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				GSON.toJson(root, writer);
			}
			LOGGER.info("[Create] Asset download consent saved as {} in {}", newState, path);
		} catch (Exception e) {
			LOGGER.warn("[Create] Could not write asset download consent to {}: {}", path, e.toString());
		}
	}
}

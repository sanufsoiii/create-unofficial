package com.simibubi.create.foundation.utility;

import java.nio.file.Path;

import dev.architectury.platform.Platform;

public class CreatePaths {
	// These are all absolute, so anything that is resolved via Path#resolve on these paths will also always be absolute
	public static final Path GAME_DIR = Platform.getGameFolder();
	public static final Path CONFIG_DIR = Platform.getConfigFolder();
	public static final Path MODS_DIR = Platform.getModsFolder();

	public static final Path SCHEMATICS_DIR = GAME_DIR.resolve("schematics");
	public static final Path UPLOADED_SCHEMATICS_DIR = SCHEMATICS_DIR.resolve("uploaded");

	private CreatePaths() {
	}
}

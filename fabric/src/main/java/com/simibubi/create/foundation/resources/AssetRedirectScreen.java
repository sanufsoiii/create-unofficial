package com.simibubi.create.foundation.resources;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game screen directing players to download official Create assets directly from CurseForge.
 */
public class AssetRedirectScreen extends Screen {
	private static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/create/files";
	private final Screen parent;

	public AssetRedirectScreen(Screen parent) {
		super(Component.literal("Create Reforged: Assets Notice"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		int btnWidth = 260;
		int btnHeight = 20;
		int centerX = this.width / 2;
		int startY = this.height / 2 + 10;

		// Button 1: Open official CurseForge page
		this.addRenderableWidget(Button.builder(Component.literal("Download Official Create (CurseForge)"), btn -> {
			try {
				Util.getPlatform().openUri(URI.create(CURSEFORGE_URL));
			} catch (Exception ignored) {
			}
		}).bounds(centerX - btnWidth / 2, startY, btnWidth, btnHeight).build());

		// Button 2: Open Resource Packs folder
		this.addRenderableWidget(Button.builder(Component.literal("Open Resource Packs Folder"), btn -> {
			try {
				Path rp = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
				Files.createDirectories(rp);
				Util.getPlatform().openFile(rp.toFile());
			} catch (Exception ignored) {
			}
		}).bounds(centerX - btnWidth / 2, startY + 26, btnWidth, btnHeight).build());

		// Button 3: Continue to game
		this.addRenderableWidget(Button.builder(Component.literal("Continue to Game"), btn -> {
			if (this.minecraft != null) {
				this.minecraft.setScreen(this.parent);
			}
		}).bounds(centerX - btnWidth / 2, startY + 54, btnWidth, btnHeight).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderMenuBackground(guiGraphics);
		guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		guiGraphics.drawCenteredString(this.font, Component.literal("Create Reforged [Unofficial]"), this.width / 2, this.height / 2 - 75, 0xFFFFAA00);
		guiGraphics.drawCenteredString(this.font, Component.literal("To respect Create's ARR license, original models and textures are not bundled."), this.width / 2, this.height / 2 - 50, 0xFFFFFFFF);
		guiGraphics.drawCenteredString(this.font, Component.literal("Please download the official Create mod and place the jar in your 'resourcepacks' folder."), this.width / 2, this.height / 2 - 35, 0xFFAAAAAA);
		guiGraphics.drawCenteredString(this.font, Component.literal("Any official Create 1.21.1 jar file functions directly as a full resource pack!"), this.width / 2, this.height / 2 - 20, 0xFF55FF55);
	}

	@Override
	public void onClose() {
		if (this.minecraft != null) {
			this.minecraft.setScreen(this.parent);
		}
	}
}

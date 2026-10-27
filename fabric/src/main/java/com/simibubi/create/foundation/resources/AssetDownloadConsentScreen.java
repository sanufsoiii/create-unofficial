package com.simibubi.create.foundation.resources;

import java.util.concurrent.CompletableFuture;



import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

/**
 * Asks the player for explicit, informed consent before anything is downloaded.
 * <p>
 * Shown once on the title screen when the textures are missing and the player
 * has not decided yet. The answer is persisted through {@link AssetDownloadConsent},
 * so the question never appears again unless the config file is deleted.
 */
public final class AssetDownloadConsentScreen {
	private static final Component TITLE = Component.literal("Download original Create textures?");
	private static final Component MESSAGE = Component.literal(
		"Create Reforged does not include Create's original textures, because they are All Rights Reserved."
			+ "\n\nIf you allow it, Minecraft will download the official Create 6.0.10 file (~70 MB) from "
			+ "cdn.modrinth.com, verify its checksum and extract the textures from it. Nothing else is "
			+ "downloaded and no data about you is sent anywhere."
			+ "\n\nYour choice is saved in config/create-assets-consent.json and can be changed at any time. "
			+ "If you decline, the mod stays fully playable but Create's blocks will be missing textures.");
	private static final Component YES = Component.literal("Download");
	private static final Component NO = Component.literal("Don't download");

	private static final Component TOAST_TITLE = Component.literal("Create Reforged");

	private AssetDownloadConsentScreen() {
	}

	public static void showIfNeeded() {
		if (!AssetDownloadConsent.needsPrompt() || !CreateAssetDownloader.needsDownload())
			return;

		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new ConfirmScreen(AssetDownloadConsentScreen::onDecision, TITLE, MESSAGE, YES, NO));
	}

	private static void onDecision(boolean accepted) {
		Minecraft mc = Minecraft.getInstance();
		mc.setScreen(new TitleScreen());

		if (accepted) {
			AssetDownloadConsent.allow();
			CompletableFuture.runAsync(() -> {
				boolean success = CreateAssetDownloader.downloadAndPrepare();
				mc.execute(() -> toast(mc, success
					? "Create textures downloaded. Restart the game to apply them."
					: "Could not download Create textures. See the game log for details."));
			});
			return;
		}

		AssetDownloadConsent.deny();
		toast(mc, "Download skipped. Create's original textures will not be used. "
			+ "You can allow the download later in config/create-assets-consent.json.");
	}

	private static void toast(Minecraft mc, String message) {
		mc.getToasts()
			.addToast(SystemToast.multiline(mc, SystemToast.SystemToastId.PERIODIC_NOTIFICATION, TOAST_TITLE,
				Component.literal(message)));
	}
}

package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import com.simibubi.create.foundation.resources.AssetDownloadConsent;
import com.simibubi.create.foundation.resources.AssetDownloadConsentScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;

/**
 * Asks for the asset download consent right after the title screen appears, so the
 * player always sees the question before any network access could happen.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	private static boolean create$consentAsked;

	@Inject(method = "init", at = @At("TAIL"))
	private void create$askForAssetDownloadConsent(CallbackInfo ci) {
		if (create$consentAsked || !AssetDownloadConsent.needsPrompt())
			return;
		create$consentAsked = true;
		Minecraft mc = Minecraft.getInstance();
		// Deferred by one tick: the title screen becomes the active screen only after init returns.
		mc.execute(AssetDownloadConsentScreen::showIfNeeded);
	}
}

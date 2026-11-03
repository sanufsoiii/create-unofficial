package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.resources.AssetRedirectScreen;
import com.simibubi.create.foundation.resources.CreateAssetPackFinder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

/**
 * Shows the asset redirect notice when Create's original assets are not found locally.
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {
	private static boolean create$redirectShown = false;

	@Inject(method = "render", at = @At("HEAD"))
	private void create$showAssetRedirectOnRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		if (create$redirectShown || CreateAssetPackFinder.hasAssets())
			return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.getOverlay() != null)
			return;
		create$redirectShown = true;
		mc.setScreen(new AssetRedirectScreen((Screen) (Object) this));
	}
}

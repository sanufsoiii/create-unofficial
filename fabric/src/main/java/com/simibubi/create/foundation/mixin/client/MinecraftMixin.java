package com.simibubi.create.foundation.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.resources.CreateAssetDownloader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.repository.PackRepository;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	private PackRepository resourcePackRepository;

	@Inject(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Options;loadSelectedResourcePacks(Lnet/minecraft/server/packs/repository/PackRepository;)V",
					shift = At.Shift.AFTER
			)
	)
	private void create$autoEnableTexturesPack(GameConfig gameConfig, CallbackInfo ci) {
		if (this.resourcePackRepository != null && this.resourcePackRepository.isAvailable(CreateAssetDownloader.PACK_ID)) {
			if (!this.resourcePackRepository.getSelectedIds().contains(CreateAssetDownloader.PACK_ID)) {
				this.resourcePackRepository.addPack(CreateAssetDownloader.PACK_ID);
			}
		}
	}
}

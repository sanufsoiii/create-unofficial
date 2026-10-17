package com.simibubi.create.foundation.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableSet;
import com.simibubi.create.foundation.pack.DynamicAssetManager;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Shadow
	@Final
	@Mutable
	private Set<RepositorySource> sources;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void create$addDynamicAssetSource(RepositorySource[] repositorySources, CallbackInfo ci) {
		if (DynamicAssetManager.isClient()) {
			java.util.LinkedHashSet<RepositorySource> newSources = new java.util.LinkedHashSet<>(this.sources);
			newSources.add(DynamicAssetManager.createRepositorySource());
			this.sources = newSources;
		}
	}
}

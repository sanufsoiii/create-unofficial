package com.tterrag.registrate.fabric;

import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_1723;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3610;

/**
 * SimpleFluidRenderHandler only gets its sprites when Fabric reloads the fluid renderer. Some mod setups ask for the
 * sprites before that happens (Moonlight soft fluids in large packs), which used to return nulls and break world
 * loading. Fill them from the block atlas on demand instead.
 */
public class LazyFluidRenderHandler extends SimpleFluidRenderHandler {
	public LazyFluidRenderHandler(class_2960 still, class_2960 flowing) {
		super(still, flowing);
	}

	@Override
	public class_1058[] getFluidSprites(class_1920 view, class_2338 pos, class_3610 state) {
		if (sprites[0] == null || sprites[1] == null) {
			try {
				class_1059 atlas = class_310.method_1551().method_1554().method_24153(class_1723.field_21668);
				if (atlas != null) reloadTextures(atlas);
			} catch (Throwable ignored) {
			}
		}
		return super.getFluidSprites(view, pos, state);
	}
}

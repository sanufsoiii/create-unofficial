package com.simibubi.create.foundation.render;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.Create;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderTypes extends RenderStateShard {

	public RenderTypes(String name, Runnable setupState, Runnable clearState) {
		super(name, setupState, clearState);
	}

	private static final RenderType ENTITY_SOLID_BLOCK_MIPPED = RenderType.create(createLayerName("entity_solid_block_mipped"),
			DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
				.setTextureState(BLOCK_SHEET_MIPPED)
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));

	private static final RenderType ENTITY_CUTOUT_BLOCK_MIPPED = RenderType.create(createLayerName("entity_cutout_block_mipped"),
			DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
				.setTextureState(BLOCK_SHEET_MIPPED)
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));

	private static final RenderType ENTITY_TRANSLUCENT_BLOCK_MIPPED = RenderType.create(createLayerName("entity_translucent_block_mipped"),
			DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
			RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER)
				.setTextureState(BLOCK_SHEET_MIPPED)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true));

	private static final RenderType ADDITIVE = RenderType.create(createLayerName("additive"), DefaultVertexFormat.BLOCK,
		VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_SOLID_SHADER)
			.setTextureState(BLOCK_SHEET)
			.setTransparencyState(ADDITIVE_TRANSPARENCY)
			.setLightmapState(LIGHTMAP)
			.createCompositeState(true));

	private static String createLayerName(String name) {
		return Create.ID + ":" + name;
	}

	public static RenderType entitySolidBlockMipped() {
		return ENTITY_SOLID_BLOCK_MIPPED;
	}

	public static RenderType entityCutoutBlockMipped() {
		return ENTITY_CUTOUT_BLOCK_MIPPED;
	}

	public static RenderType entityTranslucentBlockMipped() {
		return ENTITY_TRANSLUCENT_BLOCK_MIPPED;
	}

	public static RenderType additive() {
		return ADDITIVE;
	}

	public static BiFunction<ResourceLocation, Boolean, RenderType> TRAIN_MAP = Util.memoize(RenderTypes::getTrainMap);

	private static RenderType getTrainMap(ResourceLocation locationIn, boolean linearFiltering) {
		RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_TEXT_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(locationIn, linearFiltering, false))
			.setTransparencyState(NO_TRANSPARENCY)
			.setLightmapState(LIGHTMAP)
			.createCompositeState(false);
		return RenderType.create("create_train_map", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
			VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
	}

	private static final Function<ResourceLocation, RenderType> CHAIN = Util.memoize(location -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
			.setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
			.setTransparencyState(NO_TRANSPARENCY)
			.setLightmapState(LIGHTMAP)
			.setOverlayState(OVERLAY)
			.createCompositeState(true);
		return RenderType.create(createLayerName("chain"), DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS, 256, true, false, state);
	});

	public static RenderType chain(ResourceLocation pLocation) {
		return CHAIN.apply(pLocation);
	}

}

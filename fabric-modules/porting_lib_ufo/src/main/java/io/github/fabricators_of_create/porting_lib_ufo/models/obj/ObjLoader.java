package io.github.fabricators_of_create.porting_lib_ufo.models.obj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;

import io.github.fabricators_of_create.porting_lib_ufo.core.PortingLib;
import io.github.fabricators_of_create.porting_lib_ufo.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib_ufo.models.obj.ObjModel.ModelSettings;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

/**
 * A loader for {@link ObjModel OBJ models}.
 * <p>
 * Allows the user to enable automatic face culling, toggle quad shading, flip
 * UVs, render emissively and specify a {@link ObjMaterialLibrary material
 * library} override.
 */
public class ObjLoader implements ModelLoadingPlugin, IGeometryLoader<ObjModel> {
	public static final ResourceLocation ID = PortingLib.id("obj");
	public static final ObjLoader INSTANCE = new ObjLoader();
	public static final String OBJ_MARKER = PortingLib.id("obj_marker").toString();

	private final Map<ModelSettings, ObjModel> modelCache = Maps.newConcurrentMap();
	private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();

	@Override
	public void onInitializeModelLoader(Context ctx) {
		// called every reload, clear caches
		modelCache.clear();
		materialCache.clear();

		findModels(ctx::addModels);
		ctx.resolveModel().register(new Resolver());
	}

	/**
	 * models/misc is automatically scanned for OBJ models.
	 */
	private void findModels(Consumer<ResourceLocation> out) {
		ResourceManager manager = getResourceManager();
		manager.listResources("models/misc", id -> {
			if (id.getPath().endsWith(".json")) {
				manager.getResource(id).ifPresent(resource -> {
					if (tryLoadModelJson(id, resource) != null) {
						out.accept(id);
					}
				});
			}
			return true;
		});
	}

	private JsonObject tryLoadModelJson(ResourceLocation id, Resource resource) {
		try {
			JsonObject json = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
			if (json.has(OBJ_MARKER)) {
				return json;
			}
		} catch (IOException | IllegalStateException e) {
			PortingLib.LOGGER.error("Error loading obj model from models/misc: " + id, e);
		}
		return null;
	}

	private Either<ModelSettings, RuntimeException> tryReadSettings(JsonObject json) {
		try {
			String modelStr = GsonHelper.getAsString(json, "model");
			if (!modelStr.endsWith(".obj")) {
				modelStr = modelStr + ".obj";
			}
			ResourceLocation objLocation = ResourceLocation.parse(modelStr);
			boolean automaticCulling = json.has("automatic_culling") ? GsonHelper.getAsBoolean(json, "automatic_culling") : GsonHelper.getAsBoolean(json, "automaticCulling", true);
			boolean shadeQuads = json.has("shade_quads") ? GsonHelper.getAsBoolean(json, "shade_quads") : GsonHelper.getAsBoolean(json, "shadeQuads", true);
			boolean flipV = json.has("flip_v") ? GsonHelper.getAsBoolean(json, "flip_v") : GsonHelper.getAsBoolean(json, "flipV", true);
			boolean emissiveAmbient = json.has("emissive_ambient") ? GsonHelper.getAsBoolean(json, "emissive_ambient") : GsonHelper.getAsBoolean(json, "emissiveAmbient", true);
			String mtlOverride = json.has("mtl_override") ? GsonHelper.getAsString(json, "mtl_override") : GsonHelper.getAsString(json, "mtlOverride", null);
			return Either.left(new ModelSettings(objLocation, automaticCulling,
					shadeQuads, flipV,
					emissiveAmbient,
					mtlOverride));
		} catch (RuntimeException e) { // ID parse fail, json parse fail
			return Either.right(e);
		}
	}

	/**
	 * Load an OBJ model as a block model's geometry.
	 */
	@Override
	public ObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
		return tryReadSettings(jsonObject).map(this::loadModel, exception -> {
			throw new JsonParseException("Error loading OBJ model settings", exception);
		});
	}

	private ObjModel loadModel(ObjModel.ModelSettings settings) {
		ResourceLocation id = settings.modelLocation();
		var manager = getResourceManager();
		var resourceOpt = manager.getResource(id);
		if (resourceOpt.isEmpty() && !id.getPath().startsWith("models/")) {
			ResourceLocation altId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/" + id.getPath());
			resourceOpt = manager.getResource(altId);
		}
		Resource resource = resourceOpt
				.orElseThrow(() -> new NoSuchElementException(settings.modelLocation().toString()));
		return loadModel(resource, settings);
	}

	private ObjModel loadModel(Resource resource, ObjModel.ModelSettings settings) {
		return modelCache.computeIfAbsent(settings, data -> {
			try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
				return ObjParser.parse(tokenizer, settings);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ model", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ model", e);
			}
		});
	}

	public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
		return materialCache.computeIfAbsent(materialLocation, location -> {
			var manager = getResourceManager();
			var resourceOpt = manager.getResource(location);
			if (resourceOpt.isEmpty() && !location.getPath().startsWith("models/")) {
				ResourceLocation altLocation = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "models/" + location.getPath());
				resourceOpt = manager.getResource(altLocation);
			}
			Resource resource = resourceOpt.orElseThrow(() -> new NoSuchElementException(location.toString()));
			try (ObjTokenizer rdr = new ObjTokenizer(resource.open())) {
				return new ObjMaterialLibrary(rdr);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ material library", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ material library", e);
			}
		});
	}

	private static ResourceManager getResourceManager() {
		return Minecraft.getInstance().getResourceManager();
	}

	private class Resolver implements ModelResolver {
		@Override
		@Nullable
		public UnbakedModel resolveModel(Context context) {
			ResourceLocation id = context.id();
			var resourceManager = getResourceManager();
			return resourceManager.getResource(id).map(resource -> {
				JsonObject json = tryLoadModelJson(id, resource);
				if (json != null) {
					return tryReadSettings(json).map(settings -> {
						try {
							return loadModel(resourceManager.getResourceOrThrow(
									ResourceLocation.parse(GsonHelper.getAsString(json, "model"))), settings);
						} catch (FileNotFoundException e) {
							throw new RuntimeException(e);
						}
					}, exception -> {
						PortingLib.LOGGER.error("Error loading obj model: " + id, exception);
						return null;
					});

				}
				return null;
			}).orElse(null);
		}
	}
}

package com.simibubi.create;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.glue.SuperGlueRenderer;
import com.simibubi.create.fabric.CreateFabric;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.EntityEntry;

import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;

public class AllEntityTypes {
	private static final CreateRegistrate REGISTRATE = CreateFabric.REGISTRATE;

	public static final EntityEntry<SuperGlueEntity> SUPER_GLUE =
		register("super_glue", SuperGlueEntity::new, () -> SuperGlueRenderer::new, MobCategory.MISC, 10,
			Integer.MAX_VALUE, false, true).register();

	private static <T extends Entity> com.tterrag.registrate.builders.EntityBuilder<T, CreateRegistrate> register(
		String name, EntityFactory<T> factory,
		com.tterrag.registrate.util.nullness.NonNullSupplier<com.tterrag.registrate.util.nullness.NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
		MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire) {
		String id = Lang.asId(name);
		return REGISTRATE.entity(id, factory, group)
			.properties(b -> {
				b.trackRangeChunks(range);
				b.trackedUpdateRate(updateFrequency);
				b.forceTrackedVelocityUpdates(sendVelocity);
				if (immuneToFire)
					b.fireImmune();
			})
			.renderer(renderer);
	}

	public static void register() {
	}
}

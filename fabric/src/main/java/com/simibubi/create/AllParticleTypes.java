package com.simibubi.create;

import java.util.function.Supplier;

import com.simibubi.create.content.kinetics.fan.AirFlowParticleData;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.particle.ICustomParticleData;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public enum AllParticleTypes {
	ROTATION_INDICATOR(com.simibubi.create.content.kinetics.base.RotationIndicatorParticleData::new),
	AIR_FLOW(AirFlowParticleData::new),
	AIR(AirParticleData::new),
	STEAM_JET(com.simibubi.create.content.kinetics.steamEngine.SteamJetParticleData::new);

	private final ParticleEntry<?> entry;

	<D extends ParticleOptions> AllParticleTypes(Supplier<? extends ICustomParticleData<D>> typeFactory) {
		String name = Lang.asId(name());
		entry = new ParticleEntry<>(name, typeFactory);
	}

	public static void register() {
		for (AllParticleTypes particle : values()) {
			particle.entry.register();
		}
	}

	public static void registerFactories() {
		for (AllParticleTypes particle : values()) {
			particle.entry.registerFactory();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends ParticleType<?>> T get() {
		return (T) entry.object;
	}

	public String parameter() {
		return entry.name;
	}

	private static class ParticleEntry<D extends ParticleOptions> {
		private final String name;
		private final Supplier<? extends ICustomParticleData<D>> typeFactory;
		private final ParticleType<D> object;

		public ParticleEntry(String name, Supplier<? extends ICustomParticleData<D>> typeFactory) {
			this.name = name;
			this.typeFactory = typeFactory;
			this.object = this.typeFactory.get().createType();
		}

		public void register() {
			Registry.register(BuiltInRegistries.PARTICLE_TYPE, Create.asResource(name), object);
		}

		public void registerFactory() {
			typeFactory.get().register(object);
		}
	}
}

package com.simibubi.create.content.fluids.transfer;

import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.simibubi.create.Create;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public enum BottomlessFluidMode implements Predicate<Fluid> {
	ALLOW_ALL(Predicates.alwaysTrue()),
	DENY_ALL(Predicates.alwaysFalse()),
	ALLOW_BY_TAG(fluid -> fluid.is(TagKey.create(Registries.FLUID, Create.asResource("bottomless/allow")))),
	DENY_BY_TAG(Predicates.not(fluid -> fluid.is(TagKey.create(Registries.FLUID, Create.asResource("bottomless/deny")))));

	private final Predicate<Fluid> predicate;

	BottomlessFluidMode(Predicate<Fluid> predicate) {
		this.predicate = predicate;
	}

	@Override
	public boolean test(Fluid fluid) {
		return predicate.test(fluid);
	}
}

package com.simibubi.create.foundation.advancement;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AllTriggers {

	private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

	public static SimpleCreateTrigger addSimple(String id) {
		return add(new SimpleCreateTrigger(id));
	}

	private static <T extends CriterionTriggerBase<?>> T add(T instance) {
		triggers.add(instance);
		return instance;
	}

	public static void register() {
		triggers.forEach(ctp -> {
			Registry.register(BuiltInRegistries.TRIGGER_TYPES, ctp.getId(), ctp);
			Registry.register(BuiltInRegistries.TRIGGER_TYPES, net.minecraft.resources.ResourceLocation.withDefaultNamespace(ctp.getId().getPath()), ctp);
		});
	}

}

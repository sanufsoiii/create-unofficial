package com.simibubi.create.foundation.blockEntity;

import java.util.Map;

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SmartBlockEntityHooks {
	@ExpectPlatform
	public static void postBehaviourEvent(SmartBlockEntity blockEntity, Map<BehaviourType<?>, BlockEntityBehaviour> behaviours) {
		throw new AssertionError("@ExpectPlatform");
	}
}

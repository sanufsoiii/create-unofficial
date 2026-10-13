package com.simibubi.create.foundation.blockEntity;

import java.util.Map;

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.PlatformDispatch;

public class SmartBlockEntityHooks {
	public static void postBehaviourEvent(SmartBlockEntity blockEntity, Map<BehaviourType<?>, BlockEntityBehaviour> behaviours) {
		PlatformDispatch.call(
			"com.simibubi.create.foundation.blockEntity.SmartBlockEntityHooksImpl",
			"com.simibubi.create.foundation.blockEntity.SmartBlockEntityHooksImpl",
			"postBehaviourEvent",
			new Class<?>[]{SmartBlockEntity.class, Map.class},
			blockEntity, behaviours
		);
	}
}

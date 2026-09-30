package com.simibubi.create.foundation.blockEntity;

import java.util.Map;

import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.neoforged.neoforge.common.NeoForge;

public class SmartBlockEntityHooksImpl {
	public static void postBehaviourEvent(SmartBlockEntity blockEntity, Map<BehaviourType<?>, BlockEntityBehaviour> behaviours) {
		NeoForge.EVENT_BUS.post(new BlockEntityBehaviourEvent(blockEntity, behaviours));
	}
}

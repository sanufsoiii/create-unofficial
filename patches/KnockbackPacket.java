package com.simibubi.create.content.equipment.tool;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.tterrag.registrate.fabric.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.minecraft.class_9129;

public class KnockbackPacket extends SimplePacketBase {
	private final float yRot;
	private final float strength;

	public KnockbackPacket(float yRot, float strength) {
		this.yRot = yRot;
		this.strength = strength;
	}

	public KnockbackPacket(class_9129 buffer) {
		this.yRot = buffer.readFloat();
		this.strength = buffer.readFloat();
	}

	@Override
	public void write(class_9129 buffer) {
		buffer.method_52941(this.yRot);
		buffer.method_52941(this.strength);
	}

	@Override
	public boolean handle(SimplePacketBase.Context context) {
		// The client-only body lives in KnockbackPacketClient: referencing LocalPlayer here made the dedicated
		// server load a client class while verifying this one and crash in AllPackets.<clinit>.
		context.enqueueWork(() -> EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> KnockbackPacketClient.apply(this.strength, this.yRot)));
		return true;
	}
}

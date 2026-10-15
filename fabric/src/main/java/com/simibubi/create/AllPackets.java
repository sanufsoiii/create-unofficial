package com.simibubi.create;

import java.util.Locale;

import com.simibubi.create.content.kinetics.gauge.GaugeObservedPacket;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmPlacementPacket;
import com.simibubi.create.content.kinetics.transmission.sequencer.ConfigureSequencedGearshiftPacket;
import com.simibubi.create.content.logistics.depot.EjectorAwardPacket;
import com.simibubi.create.content.logistics.depot.EjectorElytraPacket;
import com.simibubi.create.content.logistics.depot.EjectorPlacementPacket;
import com.simibubi.create.content.logistics.depot.EjectorTriggerPacket;
import com.simibubi.create.content.logistics.funnel.FunnelFlapPacket;
import com.simibubi.create.content.logistics.tunnel.TunnelFlapPacket;
import com.simibubi.create.content.redstone.thresholdSwitch.ConfigureThresholdSwitchPacket;
import com.simibubi.create.content.schematics.cannon.ConfigureSchematicannonPacket;
import com.simibubi.create.foundation.blockEntity.RemoveBlockEntityPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.gui.menu.ClearMenuPacket;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;
import com.simibubi.create.foundation.networking.LeftClickPacket;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum AllPackets implements BasePacketPayload.PacketTypeProvider {
	// Client to Server
	CONFIGURE_SCHEMATICANNON(ConfigureSchematicannonPacket.class, ConfigureSchematicannonPacket.STREAM_CODEC),
	CONFIGURE_STOCKSWITCH(ConfigureThresholdSwitchPacket.class, ConfigureThresholdSwitchPacket.STREAM_CODEC),
	CONFIGURE_SEQUENCER(ConfigureSequencedGearshiftPacket.class, ConfigureSequencedGearshiftPacket.STREAM_CODEC),
	CLEAR_CONTAINER(ClearMenuPacket.class, ClearMenuPacket.STREAM_CODEC),
	PLACE_ARM(ArmPlacementPacket.class, ArmPlacementPacket.STREAM_CODEC),
	LEFT_CLICK(LeftClickPacket.class, LeftClickPacket.STREAM_CODEC),
	PLACE_EJECTOR(EjectorPlacementPacket.class, EjectorPlacementPacket.STREAM_CODEC),
	TRIGGER_EJECTOR(EjectorTriggerPacket.class, EjectorTriggerPacket.STREAM_CODEC),
	EJECTOR_ELYTRA(EjectorElytraPacket.class, EjectorElytraPacket.STREAM_CODEC),
	SUBMIT_GHOST_ITEM(GhostItemSubmitPacket.class, GhostItemSubmitPacket.STREAM_CODEC),
	OBSERVER_STRESSOMETER(GaugeObservedPacket.class, GaugeObservedPacket.STREAM_CODEC),
	EJECTOR_AWARD(EjectorAwardPacket.class, EjectorAwardPacket.STREAM_CODEC),
	VALUE_SETTINGS(ValueSettingsPacket.class, ValueSettingsPacket.STREAM_CODEC),
	CONFIGURE_FILTER(com.simibubi.create.content.logistics.filter.FilterScreenPacket.class, com.simibubi.create.content.logistics.filter.FilterScreenPacket.STREAM_CODEC),
	UPLOAD_SCHEMATIC(com.simibubi.create.content.schematics.packet.SchematicUploadPacket.class, com.simibubi.create.content.schematics.packet.SchematicUploadPacket.STREAM_CODEC),

	// Server to Client
	SERVER_SPEED(ServerSpeedProvider.Packet.class, ServerSpeedProvider.Packet.STREAM_CODEC),
	TUNNEL_FLAP(TunnelFlapPacket.class, TunnelFlapPacket.STREAM_CODEC),
	FUNNEL_FLAP(FunnelFlapPacket.class, FunnelFlapPacket.STREAM_CODEC),
	REMOVE_TE(RemoveBlockEntityPacket.class, RemoveBlockEntityPacket.STREAM_CODEC),
	S_PLACE_ARM(ArmPlacementPacket.ClientBoundRequest.class, ArmPlacementPacket.ClientBoundRequest.STREAM_CODEC),
	S_PLACE_EJECTOR(EjectorPlacementPacket.ClientBoundRequest.class, EjectorPlacementPacket.ClientBoundRequest.STREAM_CODEC);

	private final CatnipPacketRegistry.PacketType<?> type;

	<T extends BasePacketPayload> AllPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		String name = this.name().toLowerCase(Locale.ROOT);
		this.type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Create.asResource(name)),
			clazz, codec
		);
	}

	<T extends BasePacketPayload> AllPackets(Class<T> clazz, StreamCodec<? super ByteBuf, T> codec, Void dummy) {
		String name = this.name().toLowerCase(Locale.ROOT);
		this.type = new CatnipPacketRegistry.PacketType<>(
			new CustomPacketPayload.Type<>(Create.asResource(name)),
			clazz, codec
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
		return (CustomPacketPayload.Type<T>) this.type.type();
	}

	public static void register() {
		CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(Create.ID, CreateBuildInfo.VERSION);
		for (AllPackets packet : AllPackets.values()) {
			packetRegistry.registerPacket(packet.type);
		}
		packetRegistry.registerAllPackets();
	}
}

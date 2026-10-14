package com.simibubi.create.content.schematics.cannon;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConfigureSchematicannonPacket(Option option, boolean set) {
	public static final StreamCodec<ByteBuf, ConfigureSchematicannonPacket> STREAM_CODEC = StreamCodec.composite(
			Option.STREAM_CODEC, ConfigureSchematicannonPacket::option,
			ByteBufCodecs.BOOL, ConfigureSchematicannonPacket::set,
			ConfigureSchematicannonPacket::new
	);

	public enum Option {
		DONT_REPLACE, REPLACE_SOLID, REPLACE_ANY, REPLACE_EMPTY, SKIP_MISSING, SKIP_BLOCK_ENTITIES, PLAY, PAUSE, STOP;

		public static final StreamCodec<ByteBuf, Option> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(Option.class);
	}
}

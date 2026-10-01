package com.simibubi.create.content.trains.track;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record TrackConnectingFrom(BlockPos pos, Vec3 axis, Vec3 normal, Vec3 end) {
	public static final Codec<TrackConnectingFrom> CODEC = RecordCodecBuilder.create(i -> i.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(TrackConnectingFrom::pos),
		Vec3.CODEC.fieldOf("axis").forGetter(TrackConnectingFrom::axis),
		Vec3.CODEC.fieldOf("normal").forGetter(TrackConnectingFrom::normal),
		Vec3.CODEC.fieldOf("end").forGetter(TrackConnectingFrom::end)
	).apply(i, TrackConnectingFrom::new));

	public static final StreamCodec<ByteBuf, TrackConnectingFrom> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, TrackConnectingFrom::pos,
		CatnipStreamCodecs.VEC3, TrackConnectingFrom::axis,
		CatnipStreamCodecs.VEC3, TrackConnectingFrom::normal,
		CatnipStreamCodecs.VEC3, TrackConnectingFrom::end,
		TrackConnectingFrom::new
	);
}

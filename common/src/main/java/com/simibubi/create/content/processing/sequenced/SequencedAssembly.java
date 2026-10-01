package com.simibubi.create.content.processing.sequenced;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SequencedAssembly(ResourceLocation id, int step, float progress) {
	public static final Codec<SequencedAssembly> CODEC = RecordCodecBuilder.create(i -> i.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(SequencedAssembly::id),
		Codec.INT.fieldOf("step").forGetter(SequencedAssembly::step),
		Codec.FLOAT.fieldOf("progress").forGetter(SequencedAssembly::progress)
	).apply(i, SequencedAssembly::new));

	public static final StreamCodec<ByteBuf, SequencedAssembly> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, SequencedAssembly::id,
		ByteBufCodecs.INT, SequencedAssembly::step,
		ByteBufCodecs.FLOAT, SequencedAssembly::progress,
		SequencedAssembly::new
	);
}

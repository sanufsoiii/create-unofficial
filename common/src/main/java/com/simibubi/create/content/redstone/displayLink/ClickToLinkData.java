package com.simibubi.create.content.redstone.displayLink;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ClickToLinkData(BlockPos selectedPos, ResourceLocation selectedDim) {
	public static final Codec<ClickToLinkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		BlockPos.CODEC.fieldOf("selected_pos").forGetter(ClickToLinkData::selectedPos),
		ResourceLocation.CODEC.fieldOf("selected_dim").forGetter(ClickToLinkData::selectedDim)
	).apply(instance, ClickToLinkData::new));

	public static final StreamCodec<ByteBuf, ClickToLinkData> STREAM_CODEC = StreamCodec.composite(
	    BlockPos.STREAM_CODEC, ClickToLinkData::selectedPos,
	    ResourceLocation.STREAM_CODEC, ClickToLinkData::selectedDim,
	    ClickToLinkData::new
	);
}

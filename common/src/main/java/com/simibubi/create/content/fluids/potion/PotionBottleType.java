package com.simibubi.create.content.fluids.potion;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum PotionBottleType implements StringRepresentable {
	REGULAR, SPLASH, LINGERING;

	public static final Codec<PotionBottleType> CODEC = StringRepresentable.fromEnum(PotionBottleType::values);
	public static final StreamCodec<ByteBuf, PotionBottleType> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PotionBottleType.class);

	@Override
	public @NotNull String getSerializedName() {
		return Lang.asId(name());
	}
}

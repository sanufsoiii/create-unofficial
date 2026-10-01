package com.simibubi.create.content.equipment.clipboard;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum ClipboardType implements StringRepresentable {
	EMPTY("empty_clipboard"), WRITTEN("clipboard"), EDITING("clipboard_and_quill");

	public static final Codec<ClipboardType> CODEC = StringRepresentable.fromValues(ClipboardType::values);
	public static final StreamCodec<ByteBuf, ClipboardType> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(ClipboardType.class);

	public final String file;
	public static ResourceLocation ID = Create.asResource("clipboard_type");

	ClipboardType(String file) {
		this.file = file;
	}

	@Override
	public @NotNull String getSerializedName() {
		return Lang.asId(name());
	}
}

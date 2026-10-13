package com.simibubi.create.content.logistics.item.filter.attribute;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemAttribute {
	Codec<ItemAttribute> CODEC = getItemAttributeTypeRegistry().byNameCodec().dispatch(ItemAttribute::getType, ItemAttributeType::codec);
	StreamCodec<RegistryFriendlyByteBuf, ItemAttribute> STREAM_CODEC = ByteBufCodecs.registry(getItemAttributeTypeRegistryKey()).dispatch(ItemAttribute::getType, ItemAttributeType::streamCodec);

	@SuppressWarnings("unchecked")
	static Registry<ItemAttributeType> getItemAttributeTypeRegistry() {
		try {
			Class<?> impl = Class.forName("com.simibubi.create.content.logistics.item.filter.attribute.neoforge.ItemAttributeImpl");
			return (Registry<ItemAttributeType>) impl.getMethod("getItemAttributeTypeRegistry").invoke(null);
		} catch (ClassNotFoundException e) {
			try {
				Class<?> impl = Class.forName("com.simibubi.create.content.logistics.item.filter.attribute.fabric.ItemAttributeImpl");
				return (Registry<ItemAttributeType>) impl.getMethod("getItemAttributeTypeRegistry").invoke(null);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static ResourceKey<Registry<ItemAttributeType>> getItemAttributeTypeRegistryKey() {
		try {
			Class<?> impl = Class.forName("com.simibubi.create.content.logistics.item.filter.attribute.neoforge.ItemAttributeImpl");
			return (ResourceKey<Registry<ItemAttributeType>>) impl.getMethod("getItemAttributeTypeRegistryKey").invoke(null);
		} catch (ClassNotFoundException e) {
			try {
				Class<?> impl = Class.forName("com.simibubi.create.content.logistics.item.filter.attribute.fabric.ItemAttributeImpl");
				return (ResourceKey<Registry<ItemAttributeType>>) impl.getMethod("getItemAttributeTypeRegistryKey").invoke(null);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static CompoundTag saveStatic(ItemAttribute attribute, HolderLookup.Provider registries) {
		CompoundTag nbt = new CompoundTag();
		nbt.put("attribute", CatnipCodecUtils.encode(CODEC, registries, attribute).orElseThrow());
		return nbt;
	}

	@Nullable
	static ItemAttribute loadStatic(CompoundTag nbt, HolderLookup.Provider registries) {
		return CatnipCodecUtils.decodeOrNull(CODEC, registries, nbt.get("attribute"));
	}

	static List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
		List<ItemAttribute> attributes = new ArrayList<>();
		for (ItemAttributeType type : getItemAttributeTypeRegistry()) {
			attributes.addAll(type.getAllAttributes(stack, level));
		}
		return attributes;
	}

	boolean appliesTo(ItemStack stack, Level world);

	default ItemStack[] getMatchingItemStacks() {
		return null;
	}

	ItemAttributeType getType();

	@Environment(EnvType.CLIENT)
	default MutableComponent format(boolean inverted) {
		return CreateLang.translateDirect("item_attributes." + getTranslationKey() + (inverted ? ".inverted" : ""),
			getTranslationParameters());
	}

	String getTranslationKey();

	default Object[] getTranslationParameters() {
		return new String[0];
	}

	record ItemAttributeEntry(ItemAttribute attribute, boolean inverted) {
		public static final Codec<ItemAttributeEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
			ItemAttribute.CODEC.fieldOf("attribute").forGetter(ItemAttributeEntry::attribute),
			Codec.BOOL.fieldOf("inverted").forGetter(ItemAttributeEntry::inverted)
		).apply(i, ItemAttributeEntry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeEntry> STREAM_CODEC = StreamCodec.composite(
			ItemAttribute.STREAM_CODEC, ItemAttributeEntry::attribute,
			ByteBufCodecs.BOOL, ItemAttributeEntry::inverted,
			ItemAttributeEntry::new
		);
	}
}

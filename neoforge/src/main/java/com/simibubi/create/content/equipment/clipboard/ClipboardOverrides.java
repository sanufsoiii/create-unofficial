package com.simibubi.create.content.equipment.clipboard;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;

import com.simibubi.create.content.equipment.clipboard.ClipboardType;

public class ClipboardOverrides {

	@OnlyIn(Dist.CLIENT)
	public static void registerModelOverridesClient(ClipboardBlockItem item) {
		ItemProperties.register(item, ClipboardType.ID, (pStack, pLevel, pEntity, pSeed) ->
			pStack.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY).type().ordinal()
		);
	}

	public static ItemModelBuilder addOverrideModels(DataGenContext<Item, ClipboardBlockItem> c,
		RegistrateItemModelProvider p) {
		ItemModelBuilder builder = p.generated(c::get);
		for (ClipboardType type : ClipboardType.values()) {
			int i = type.ordinal();
			builder.override()
					.predicate(ClipboardType.ID, i)
					.model(p.getBuilder(c.getName() + "_" + i)
							.parent(new UncheckedModelFile("item/generated"))
							.texture("layer0", Create.asResource("item/" + type.file)))
					.end();
		}
		return builder;
	}

}

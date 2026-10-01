package com.simibubi.create.content.logistics.tableCloth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Unmodifiable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;

public record ShoppingList(@Unmodifiable List<IntAttached<BlockPos>> purchases, UUID shopOwner, UUID shopNetwork) {
	public static final Codec<ShoppingList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.list(IntAttached.codec(BlockPos.CODEC)).fieldOf("purchases").forGetter(ShoppingList::purchases),
		UUIDUtil.CODEC.fieldOf("shop_owner").forGetter(ShoppingList::shopOwner),
		UUIDUtil.CODEC.fieldOf("shop_network").forGetter(ShoppingList::shopNetwork)
	).apply(instance, ShoppingList::new));

	public static final StreamCodec<ByteBuf, ShoppingList> STREAM_CODEC = StreamCodec.composite(
		CatnipStreamCodecBuilders.list(IntAttached.streamCodec(BlockPos.STREAM_CODEC)), ShoppingList::purchases,
		UUIDUtil.STREAM_CODEC, ShoppingList::shopOwner,
		UUIDUtil.STREAM_CODEC, ShoppingList::shopNetwork,
		ShoppingList::new
	);

	public ShoppingList duplicate() {
		return new ShoppingList(new ArrayList<>(purchases.stream()
			.map(ia -> IntAttached.with(ia.getFirst(), ia.getSecond()))
			.toList()), shopOwner, shopNetwork);
	}

	public int getPurchases(BlockPos clothPos) {
		for (IntAttached<BlockPos> entry : purchases)
			if (entry.getSecond()
				.equals(clothPos))
				return entry.getFirst();
		return 0;
	}

	public static class Mutable {
		private final List<IntAttached<BlockPos>> purchases = new ArrayList<>();
		private final UUID shopOwner;
		private final UUID shopNetwork;

		public Mutable(ShoppingList list) {
			this.purchases.addAll(list.purchases);
			this.shopOwner = list.shopOwner;
			this.shopNetwork = list.shopNetwork;
		}

		// Y value of clothPos is pixel perfect (x16)
		public void addPurchases(BlockPos clothPos, int amount) {
			for (IntAttached<BlockPos> entry : purchases) {
				if (clothPos.equals(entry.getValue())) {
					entry.setFirst(entry.getFirst() + amount);
					return;
				}
			}
			purchases.add(IntAttached.with(amount, clothPos));
		}

		public ShoppingList toImmutable() {
			return new ShoppingList(purchases, shopOwner, shopNetwork);
		}
	}
}

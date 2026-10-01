package com.simibubi.create.content.logistics.stockTicker;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.BigItemStack;

import net.minecraft.world.item.ItemStack;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Package ordering context containing additional information of package orders.
 */
public record PackageOrderWithCrafts(PackageOrder orderedStacks, List<CraftingEntry> orderedCrafts) {
	public static final Codec<PackageOrderWithCrafts> CODEC = Codec.withAlternative(
		RecordCodecBuilder.create(i -> i.group(
			PackageOrder.CODEC.fieldOf("ordered_stacks").forGetter(PackageOrderWithCrafts::orderedStacks),
			CraftingEntry.CODEC.listOf().fieldOf("ordered_crafts").forGetter(PackageOrderWithCrafts::orderedCrafts)
		).apply(i, PackageOrderWithCrafts::new)),

		// Legacy format (6.0.0 - 6.0.2)
		RecordCodecBuilder.create(instance -> instance.group(
			Codec.list(BigItemStack.CODEC).fieldOf("entries").forGetter(PackageOrderWithCrafts::stacks)
		).apply(instance, PackageOrderWithCrafts::simple))
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, PackageOrderWithCrafts> STREAM_CODEC =
		StreamCodec.composite(
			PackageOrder.STREAM_CODEC, s -> s.orderedStacks,
			CatnipStreamCodecBuilders.list(CraftingEntry.STREAM_CODEC), s -> s.orderedCrafts,
			PackageOrderWithCrafts::new);

	public static PackageOrderWithCrafts empty() {
		return new PackageOrderWithCrafts(PackageOrder.empty(), List.of());
	}

	public static PackageOrderWithCrafts simple(List<BigItemStack> orderedStacks) {
		return new PackageOrderWithCrafts(new PackageOrder(orderedStacks), List.of());
	}

	public static PackageOrderWithCrafts singleRecipe(List<BigItemStack> pattern) {
		return new PackageOrderWithCrafts(PackageOrder.empty(), List.of(new CraftingEntry(new PackageOrder(pattern), 1)));
	}

	public record CraftingEntry(PackageOrder pattern, int count) {
		public static final Codec<CraftingEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
			PackageOrder.CODEC.fieldOf("pattern").forGetter(CraftingEntry::pattern),
			Codec.INT.fieldOf("count").forGetter(CraftingEntry::count)
		).apply(i, CraftingEntry::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, CraftingEntry> STREAM_CODEC = StreamCodec.composite(
			PackageOrder.STREAM_CODEC, s -> s.pattern,
			ByteBufCodecs.VAR_INT, s -> s.count,
			CraftingEntry::new
		);
	}

	public static boolean hasCraftingInformation(PackageOrderWithCrafts context) {
        if (context == null)
            return false;
		// Only a valid crafting packet if it contains exactly one recipe
        return context.orderedCrafts.size() == 1;
    }

	public List<BigItemStack> getCraftingInformation() {
    	return orderedCrafts.get(0).pattern.stacks();
    }

	public List<BigItemStack> stacks() {
    	return orderedStacks.stacks();
    }

    public boolean isEmpty() {
        return orderedStacks.isEmpty();
    }

    public boolean orderedStacksMatchOrderedRecipes() {
    	if (orderedCrafts.isEmpty())
    		return false;

		java.util.Map<net.minecraft.world.item.Item, List<BigItemStack>> stacks = new java.util.IdentityHashMap<>();
		java.util.Map<net.minecraft.world.item.Item, List<BigItemStack>> crafts = new java.util.IdentityHashMap<>();

		stacks().forEach(bis -> addToMap(stacks, bis.stack, bis.count));
    	orderedCrafts.forEach(ce -> ce.pattern.stacks().forEach(bis -> addToMap(crafts, bis.stack, bis.count * ce.count)));

		List<BigItemStack> stackEntries = flattenMap(stacks);
		List<BigItemStack> craftEntries = flattenMap(crafts);
		if (stackEntries.size() != craftEntries.size())
    		return false;
		for (BigItemStack bis : stackEntries)
			if (getCountOf(crafts, bis.stack) != bis.count)
				return false;
    	return true;
    }

	private static void addToMap(java.util.Map<net.minecraft.world.item.Item, List<BigItemStack>> map, ItemStack stack, int count) {
		if (count == 0 || stack.isEmpty()) return;
		List<BigItemStack> list = map.computeIfAbsent(stack.getItem(), k -> new java.util.ArrayList<>());
		for (BigItemStack existing : list) {
			if (ItemStack.isSameItemSameComponents(existing.stack, stack)) {
				existing.count += count;
				return;
			}
		}
		list.add(new BigItemStack(stack.copyWithCount(1), count));
	}

	private static List<BigItemStack> flattenMap(java.util.Map<net.minecraft.world.item.Item, List<BigItemStack>> map) {
		List<BigItemStack> list = new java.util.ArrayList<>();
		map.values().forEach(list::addAll);
		return list;
	}

	private static int getCountOf(java.util.Map<net.minecraft.world.item.Item, List<BigItemStack>> map, ItemStack stack) {
		List<BigItemStack> list = map.get(stack.getItem());
		if (list == null) return 0;
		for (BigItemStack bis : list) {
			if (ItemStack.isSameItemSameComponents(bis.stack, stack))
				return bis.count;
		}
		return 0;
	}
}

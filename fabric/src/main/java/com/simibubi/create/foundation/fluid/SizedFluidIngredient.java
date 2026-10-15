package com.simibubi.create.foundation.fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class SizedFluidIngredient {

	public interface FluidIngredient extends Predicate<FluidStack> {
		boolean test(FluidStack stack);

		FluidStack[] getStacks();

		static FluidIngredient empty() {
			return EmptyFluidIngredient.INSTANCE;
		}

		static FluidIngredient of(Fluid fluid) {
			return new SingleFluidIngredient(fluid.builtInRegistryHolder(), DataComponentPatch.EMPTY);
		}

		static FluidIngredient single(FluidStack stack) {
			return new SingleFluidIngredient(stack.getFluidHolder(), stack.getComponentsPatch());
		}

		static FluidIngredient tag(TagKey<Fluid> tag) {
			return new TagFluidIngredient(tag);
		}
	}

	public static final class SingleFluidIngredient implements FluidIngredient {
		private final Holder<Fluid> fluid;
		private final DataComponentPatch patch;
		@Nullable
		private FluidStack[] cached;

		public SingleFluidIngredient(Holder<Fluid> fluid, DataComponentPatch patch) {
			this.fluid = fluid;
			this.patch = patch;
		}

		public Holder<Fluid> getFluidHolder() {
			return fluid;
		}

		public DataComponentPatch getPatch() {
			return patch;
		}

		@Override
		public boolean test(FluidStack stack) {
			if (!stack.is(fluid))
				return false;
			if (!patch.isEmpty())
				return stack.getComponentsPatch().equals(patch);
			return true;
		}

		@Override
		public FluidStack[] getStacks() {
			if (cached == null) {
				cached = new FluidStack[]{ new FluidStack(fluid, 1000, patch) };
			}
			return cached;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof SingleFluidIngredient that)) return false;
			return Objects.equals(fluid, that.fluid) && Objects.equals(patch, that.patch);
		}

		@Override
		public int hashCode() {
			return Objects.hash(fluid, patch);
		}

		@Override
		public String toString() {
			return "SingleFluid[" + fluid.getRegisteredName() + "]";
		}
	}

	public static final class TagFluidIngredient implements FluidIngredient {
		private final TagKey<Fluid> tag;
		@Nullable
		private FluidStack[] cached;

		public TagFluidIngredient(TagKey<Fluid> tag) {
			this.tag = tag;
		}

		public TagKey<Fluid> getTag() {
			return tag;
		}

		@Override
		public boolean test(FluidStack stack) {
			return stack.is(tag);
		}

		@Override
		public FluidStack[] getStacks() {
			if (cached == null) {
				cached = BuiltInRegistries.FLUID.getTag(tag)
					.map(set -> set.stream().map(h -> new FluidStack(h, 1000)).toArray(FluidStack[]::new))
					.orElseGet(() -> new FluidStack[0]);
			}
			return cached;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof TagFluidIngredient that)) return false;
			return Objects.equals(tag, that.tag);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag);
		}

		@Override
		public String toString() {
			return "TagFluid[#" + tag.location() + "]";
		}
	}

	public static final class EmptyFluidIngredient implements FluidIngredient {
		public static final EmptyFluidIngredient INSTANCE = new EmptyFluidIngredient();

		private EmptyFluidIngredient() {}

		@Override
		public boolean test(FluidStack stack) {
			return false;
		}

		@Override
		public FluidStack[] getStacks() {
			return new FluidStack[0];
		}

		@Override
		public String toString() {
			return "EmptyFluid[]";
		}
	}

	public static final class CompoundFluidIngredient implements FluidIngredient {
		private final List<FluidIngredient> children;
		@Nullable
		private FluidStack[] cached;

		public CompoundFluidIngredient(List<FluidIngredient> children) {
			this.children = children;
		}

		public List<FluidIngredient> getChildren() {
			return children;
		}

		@Override
		public boolean test(FluidStack stack) {
			for (FluidIngredient child : children) {
				if (child.test(stack))
					return true;
			}
			return false;
		}

		@Override
		public FluidStack[] getStacks() {
			if (cached == null) {
				List<FluidStack> list = new ArrayList<>();
				for (FluidIngredient child : children) {
					Collections.addAll(list, child.getStacks());
				}
				cached = list.toArray(FluidStack[]::new);
			}
			return cached;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CompoundFluidIngredient that)) return false;
			return Objects.equals(children, that.children);
		}

		@Override
		public int hashCode() {
			return Objects.hash(children);
		}
	}

	private static final Codec<FluidIngredient> INGREDIENT_SINGLE = RecordCodecBuilder.create(i -> i.group(
		BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("fluid").forGetter(s -> ((SingleFluidIngredient) s).fluid),
		DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(s -> ((SingleFluidIngredient) s).patch)
	).apply(i, SingleFluidIngredient::new));

	private static final Codec<FluidIngredient> INGREDIENT_TAG = RecordCodecBuilder.create(i -> i.group(
		TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(s -> ((TagFluidIngredient) s).tag)
	).apply(i, TagFluidIngredient::new));

	private static final Codec<FluidIngredient> INGREDIENT_FLUID_TAG_ALT = RecordCodecBuilder.create(i -> i.group(
		TagKey.codec(Registries.FLUID).fieldOf("fluid_tag").forGetter(s -> ((TagFluidIngredient) s).tag)
	).apply(i, TagFluidIngredient::new));

	public static final Codec<FluidIngredient> INGREDIENT_CODEC = Codec.withAlternative(
		INGREDIENT_SINGLE,
		Codec.withAlternative(INGREDIENT_TAG, INGREDIENT_FLUID_TAG_ALT)
	);

	// Flat sized codec: {"fluid": "...", "amount": 250} or {"tag": "...", "amount": 250}
	private static final Codec<SizedFluidIngredient> FLAT_SINGLE = RecordCodecBuilder.create(i -> i.group(
		BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("fluid").forGetter(s -> ((SingleFluidIngredient) s.ingredient).fluid),
		DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(s -> ((SingleFluidIngredient) s.ingredient).patch),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, (fluid, patch, amount) -> new SizedFluidIngredient(new SingleFluidIngredient(fluid, patch), amount)));

	private static final Codec<SizedFluidIngredient> FLAT_TAG = RecordCodecBuilder.create(i -> i.group(
		TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(s -> ((TagFluidIngredient) s.ingredient).tag),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, (tag, amount) -> new SizedFluidIngredient(new TagFluidIngredient(tag), amount)));

	private static final Codec<SizedFluidIngredient> FLAT_FLUID_TAG = RecordCodecBuilder.create(i -> i.group(
		TagKey.codec(Registries.FLUID).fieldOf("fluid_tag").forGetter(s -> ((TagFluidIngredient) s.ingredient).tag),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, (tag, amount) -> new SizedFluidIngredient(new TagFluidIngredient(tag), amount)));

	// Legacy typed codec: {"type": "fluid_stack", "fluid": "...", "amount": 250} or {"type": "fluid_tag", ...}
	private static final Codec<SizedFluidIngredient> TYPED_STACK = RecordCodecBuilder.create(i -> i.group(
		Codec.STRING.optionalFieldOf("type", "fluid_stack").forGetter(s -> "fluid_stack"),
		BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("fluid").forGetter(s -> ((SingleFluidIngredient) s.ingredient).fluid),
		DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(s -> ((SingleFluidIngredient) s.ingredient).patch),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, (type, fluid, patch, amount) -> new SizedFluidIngredient(new SingleFluidIngredient(fluid, patch), amount)));

	private static final Codec<SizedFluidIngredient> TYPED_TAG = RecordCodecBuilder.create(i -> i.group(
		Codec.STRING.optionalFieldOf("type", "fluid_tag").forGetter(s -> "fluid_tag"),
		TagKey.codec(Registries.FLUID).fieldOf("fluid_tag").forGetter(s -> ((TagFluidIngredient) s.ingredient).tag),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, (type, tag, amount) -> new SizedFluidIngredient(new TagFluidIngredient(tag), amount)));

	// Nested codec: {"ingredient": {...}, "amount": 250}
	private static final Codec<SizedFluidIngredient> NESTED = RecordCodecBuilder.create(i -> i.group(
		INGREDIENT_CODEC.fieldOf("ingredient").forGetter(SizedFluidIngredient::ingredient),
		Codec.INT.optionalFieldOf("amount", 1000).forGetter(SizedFluidIngredient::amount)
	).apply(i, SizedFluidIngredient::new));

	public static final Codec<SizedFluidIngredient> CODEC = Codec.withAlternative(
		NESTED,
		Codec.withAlternative(
			FLAT_SINGLE,
			Codec.withAlternative(
				FLAT_TAG,
				Codec.withAlternative(FLAT_FLUID_TAG, Codec.withAlternative(TYPED_STACK, TYPED_TAG))
			)
		)
	);

	public static final Codec<SizedFluidIngredient> FLAT_CODEC = CODEC;

	public static final StreamCodec<RegistryFriendlyByteBuf, SizedFluidIngredient> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public SizedFluidIngredient decode(RegistryFriendlyByteBuf buf) {
			byte type = buf.readByte();
			int amount = buf.readVarInt();
			if (type == 0) {
				Holder<Fluid> holder = ByteBufCodecs.holderRegistry(Registries.FLUID).decode(buf);
				DataComponentPatch patch = DataComponentPatch.STREAM_CODEC.decode(buf);
				return new SizedFluidIngredient(new SingleFluidIngredient(holder, patch), amount);
			} else if (type == 1) {
				ResourceLocation tagId = buf.readResourceLocation();
				TagKey<Fluid> tag = TagKey.create(Registries.FLUID, tagId);
				return new SizedFluidIngredient(new TagFluidIngredient(tag), amount);
			} else if (type == 2) {
				return new SizedFluidIngredient(EmptyFluidIngredient.INSTANCE, amount);
			} else {
				int size = buf.readVarInt();
				List<FluidIngredient> list = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					byte childType = buf.readByte();
					if (childType == 0) {
						Holder<Fluid> holder = ByteBufCodecs.holderRegistry(Registries.FLUID).decode(buf);
						DataComponentPatch patch = DataComponentPatch.STREAM_CODEC.decode(buf);
						list.add(new SingleFluidIngredient(holder, patch));
					} else if (childType == 1) {
						ResourceLocation tagId = buf.readResourceLocation();
						list.add(new TagFluidIngredient(TagKey.create(Registries.FLUID, tagId)));
					}
				}
				return new SizedFluidIngredient(new CompoundFluidIngredient(list), amount);
			}
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, SizedFluidIngredient ingredient) {
			if (ingredient.ingredient instanceof SingleFluidIngredient single) {
				buf.writeByte(0);
				buf.writeVarInt(ingredient.amount);
				ByteBufCodecs.holderRegistry(Registries.FLUID).encode(buf, single.fluid);
				DataComponentPatch.STREAM_CODEC.encode(buf, single.patch);
			} else if (ingredient.ingredient instanceof TagFluidIngredient tag) {
				buf.writeByte(1);
				buf.writeVarInt(ingredient.amount);
				buf.writeResourceLocation(tag.tag.location());
			} else if (ingredient.ingredient instanceof CompoundFluidIngredient compound) {
				buf.writeByte(3);
				buf.writeVarInt(ingredient.amount);
				buf.writeVarInt(compound.children.size());
				for (FluidIngredient child : compound.children) {
					if (child instanceof SingleFluidIngredient single) {
						buf.writeByte(0);
						ByteBufCodecs.holderRegistry(Registries.FLUID).encode(buf, single.fluid);
						DataComponentPatch.STREAM_CODEC.encode(buf, single.patch);
					} else if (child instanceof TagFluidIngredient tag) {
						buf.writeByte(1);
						buf.writeResourceLocation(tag.tag.location());
					} else {
						buf.writeByte(2);
					}
				}
			} else {
				buf.writeByte(2);
				buf.writeVarInt(ingredient.amount);
			}
		}
	};

	public static SizedFluidIngredient of(Fluid fluid, int amount) {
		return new SizedFluidIngredient(FluidIngredient.of(fluid), amount);
	}

	public static SizedFluidIngredient of(FluidStack stack) {
		return new SizedFluidIngredient(FluidIngredient.single(stack), (int) stack.getAmount());
	}

	public static SizedFluidIngredient of(TagKey<Fluid> tag, int amount) {
		return new SizedFluidIngredient(FluidIngredient.tag(tag), amount);
	}

	private final FluidIngredient ingredient;
	private final int amount;

	@Nullable
	private FluidStack[] cachedStacks;

	public SizedFluidIngredient(FluidIngredient ingredient, int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Size must be positive");
		}
		this.ingredient = ingredient;
		this.amount = amount;
	}

	public FluidIngredient ingredient() {
		return ingredient;
	}

	public int amount() {
		return amount;
	}

	public boolean test(FluidStack stack) {
		return ingredient.test(stack) && stack.getAmount() >= amount;
	}

	public FluidStack[] getFluids() {
		if (cachedStacks == null) {
			cachedStacks = Stream.of(ingredient.getStacks())
				.map(s -> s.copyWithAmount(amount))
				.toArray(FluidStack[]::new);
		}
		return cachedStacks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SizedFluidIngredient other)) return false;
		return amount == other.amount && ingredient.equals(other.ingredient);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredient, amount);
	}

	@Override
	public String toString() {
		return amount + "mB " + ingredient;
	}
}

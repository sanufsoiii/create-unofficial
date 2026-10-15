package com.simibubi.create.foundation.advancement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.simibubi.create.Create;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CreateAdvancement {

	static final ResourceLocation BACKGROUND = Create.asResource("textures/gui/advancements.png");
	static final String LANG = "advancement." + Create.ID + ".";
	static final String SECRET_SUFFIX = "\n\u00A77(Hidden Advancement)";

	private final Advancement.Builder mcBuilder = Advancement.Builder.advancement();
	private SimpleCreateTrigger builtinTrigger;
	private CreateAdvancement parent;
	private final Builder createBuilder = new Builder();

	AdvancementHolder datagenResult;

	private String id;
	private String title;
	private String description;

	public CreateAdvancement(String id, UnaryOperator<Builder> b) {
		this.id = id;

		b.apply(createBuilder);

		if (!createBuilder.externalTrigger) {
			builtinTrigger = AllTriggers.addSimple(id + "_builtin");
			mcBuilder.addCriterion("0", builtinTrigger.createCriterion(builtinTrigger.instance()));
		}

		if (createBuilder.type == TaskType.SECRET)
			description += SECRET_SUFFIX;
	}

	private String titleKey() {
		return LANG + id;
	}

	private String descriptionKey() {
		return titleKey() + ".desc";
	}

	public boolean isAlreadyAwardedTo(Player player) {
		if (!(player instanceof ServerPlayer sp))
			return true;
		AdvancementHolder advancement = sp.getServer()
			.getAdvancements()
			.get(Create.asResource(id));
		if (advancement == null)
			return true;
		return sp.getAdvancements()
			.getOrStartProgress(advancement)
			.isDone();
	}

	public void awardTo(Player player) {
		if (!(player instanceof ServerPlayer sp))
			return;
		if (builtinTrigger == null)
			throw new UnsupportedOperationException(
				"Advancement " + id + " uses external Triggers, it cannot be awarded directly");
		builtinTrigger.trigger(sp);
	}

	public void save(Consumer<AdvancementHolder> t, HolderLookup.Provider registries) {
		if (parent != null)
			mcBuilder.parent(parent.datagenResult);

		ItemStack iconStack = createBuilder.getIcon(registries);

		for (Consumer<Advancement.Builder> action : createBuilder.criteriaActions) {
			action.accept(mcBuilder);
		}

		mcBuilder.display(iconStack, Component.translatable(titleKey()),
			Component.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
			id.equals("root") ? BACKGROUND : null, createBuilder.type.advancementType, createBuilder.type.toast,
			createBuilder.type.announce, createBuilder.type.hide);

		datagenResult = mcBuilder.save(t, Create.asResource(id)
			.toString());
	}

	public void provideLang(BiConsumer<String, String> consumer) {
		consumer.accept(titleKey(), title);
		consumer.accept(descriptionKey(), description);
	}

	public static enum TaskType {

		SILENT(AdvancementType.TASK, false, false, false),
		NORMAL(AdvancementType.TASK, true, false, false),
		NOISY(AdvancementType.TASK, true, true, false),
		EXPERT(AdvancementType.GOAL, true, true, false),
		SECRET(AdvancementType.GOAL, true, true, true),

		;

		private final AdvancementType advancementType;
		private final boolean toast;
		private final boolean announce;
		private final boolean hide;

		TaskType(AdvancementType advancementType, boolean toast, boolean announce, boolean hide) {
			this.advancementType = advancementType;
			this.toast = toast;
			this.announce = announce;
			this.hide = hide;
		}
	}

	public class Builder {

		private TaskType type = TaskType.NORMAL;
		private boolean externalTrigger;
		private int keyIndex;
		private ItemStack icon;
		private Supplier<ItemStack> iconSupplier;
		private Function<Provider, ItemStack> func;
		final List<Consumer<Advancement.Builder>> criteriaActions = new ArrayList<>();

		public Builder special(TaskType type) {
			this.type = type;
			return this;
		}

		public Builder after(CreateAdvancement other) {
			CreateAdvancement.this.parent = other;
			return this;
		}

		public Builder icon(ItemLike item) {
			this.iconSupplier = () -> new ItemStack(item);
			return this;
		}

		public Builder icon(ItemStack stack) {
			this.icon = stack;
			return this;
		}

		public Builder icon(Function<Provider, ItemStack> func) {
			this.func = func;
			return this;
		}

		public ItemStack getIcon(HolderLookup.Provider registries) {
			if (func != null && registries != null)
				return func.apply(registries);
			if (iconSupplier != null)
				return iconSupplier.get();
			if (icon != null)
				return icon;
			return ItemStack.EMPTY;
		}

		public Builder title(String title) {
			CreateAdvancement.this.title = title;
			return this;
		}

		public Builder description(String description) {
			CreateAdvancement.this.description = description;
			return this;
		}

		public Builder whenBlockPlaced(Block block) {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> b.addCriterion(String.valueOf(index), ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(block)));
			return this;
		}

		public Builder whenIconCollected() {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> {
				ItemStack stack = getIcon(null);
				b.addCriterion(String.valueOf(index), InventoryChangeTrigger.TriggerInstance.hasItems(stack.getItem()));
			});
			return this;
		}

		public Builder whenItemCollected(ItemLike itemProvider) {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> b.addCriterion(String.valueOf(index), InventoryChangeTrigger.TriggerInstance.hasItems(itemProvider)));
			return this;
		}

		public Builder whenItemCollected(TagKey<Item> tag) {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> b.addCriterion(String.valueOf(index), InventoryChangeTrigger.TriggerInstance
				.hasItems(ItemPredicate.Builder.item().of(tag).build())));
			return this;
		}

		public Builder awardedForFree() {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> b.addCriterion(String.valueOf(index), InventoryChangeTrigger.TriggerInstance.hasItems(new ItemLike[] {})));
			return this;
		}

		public Builder externalTrigger(Criterion<?> trigger) {
			externalTrigger = true;
			int index = keyIndex++;
			criteriaActions.add(b -> b.addCriterion(String.valueOf(index), trigger));
			return this;
		}

	}

}

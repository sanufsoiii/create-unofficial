package com.simibubi.create.content.logistics.filter;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.simibubi.create.AllKeys;
import com.simibubi.create.content.logistics.box.PackageItemHelper;
import com.simibubi.create.foundation.item.CommonItemHelper;
import com.simibubi.create.foundation.recipe.SupportsItemCopying;

import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public abstract class FilterItem extends Item implements MenuProvider, SupportsItemCopying {
	public static ListFilterItem regular(Properties properties) {
		return new ListFilterItem(properties);
	}

	public static AttributeFilterItem attribute(Properties properties) {
		return new AttributeFilterItem(properties);
	}

	public static PackageFilterItem address(Properties properties) {
		return new PackageFilterItem(properties);
	}

	protected FilterItem(Properties properties) {
		super(properties);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (context.getPlayer() == null)
			return InteractionResult.PASS;
		return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (AllKeys.shiftDown())
			return;
		List<Component> makeSummary = makeSummary(stack);
		if (makeSummary.isEmpty())
			return;
		tooltip.add(CommonComponents.SPACE);
		tooltip.addAll(makeSummary);
	}

	public abstract List<Component> makeSummary(ItemStack filter);

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
			if (!world.isClientSide && player instanceof ServerPlayer serverPlayer)
				FilterMenuFactory.openFilterMenu(serverPlayer, this, heldItem);
			return InteractionResultHolder.success(heldItem);
		}
		return InteractionResultHolder.pass(heldItem);
	}

	@Override
	public abstract AbstractContainerMenu createMenu(int id, Inventory inv, Player player);

	@Override
	public Component getDisplayName() {
		return getDescription();
	}

	public static boolean testDirect(ItemStack filter, ItemStack stack, boolean matchNBT) {
		if (matchNBT) {
			if (PackageItemHelper.isPackage(filter) && PackageItemHelper.isPackage(stack))
				return doPackagesHaveSameData(filter, stack);

			return ItemStack.isSameItemSameComponents(filter, stack);
		}

		if (PackageItemHelper.isPackage(filter) && PackageItemHelper.isPackage(stack))
			return true;

		return CommonItemHelper.sameItem(filter, stack);
	}

	public static boolean doPackagesHaveSameData(@NotNull ItemStack a, @NotNull ItemStack b) {
		return PackageItemHelper.doPackagesHaveSameData(a, b);
	}

	public abstract DataComponentType<?> getComponentType();

	public abstract FilterItemStack makeStackWrapper(ItemStack filter);

	public abstract ItemStack[] getFilterItems(ItemStack stack);
}

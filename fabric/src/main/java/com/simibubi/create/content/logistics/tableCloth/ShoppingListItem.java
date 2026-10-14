package com.simibubi.create.content.logistics.tableCloth;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ShoppingListItem extends Item {
	public static Couple<InventorySummary> bakeEntries(ShoppingList list, LevelAccessor level, @Nullable BlockPos clothPosToIgnore) {
		if (level == null)
			return null;
		InventorySummary input = new InventorySummary();
		InventorySummary output = new InventorySummary();

		for (IntAttached<BlockPos> entry : list.purchases()) {
			if (clothPosToIgnore != null && clothPosToIgnore.equals(entry.getValue()))
				continue;
			if (!(level.getBlockEntity(entry.getValue()) instanceof TableClothBlockEntity dcbe))
				continue;
			input.add(dcbe.getPaymentItem(), dcbe.getPaymentAmount() * entry.getFirst());
			for (BigItemStack stackEntry : dcbe.requestData.encodedRequest().stacks())
				output.add(stackEntry.stack, stackEntry.count * entry.getFirst());
		}

		return Couple.create(output, input);
	}

	public ShoppingListItem(Properties pProperties) {
		super(pProperties);
	}

	public static ShoppingList getList(ItemStack stack) {
		return stack.get(AllDataComponents.SHOPPING_LIST);
	}

	public static ItemStack saveList(ItemStack stack, ShoppingList list, String address) {
		stack.set(AllDataComponents.SHOPPING_LIST, list);
		stack.set(AllDataComponents.SHOPPING_LIST_ADDRESS, address);
		return stack;
	}

	public static String getAddress(ItemStack stack) {
		return stack.getOrDefault(AllDataComponents.SHOPPING_LIST_ADDRESS, "");
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
								TooltipFlag tooltipFlag) {
		ShoppingList list = getList(stack);

		if (list != null) {
			Level level = null;
			if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
				level = net.minecraft.client.Minecraft.getInstance().level;
			}
			Couple<InventorySummary> lists = bakeEntries(list, level, null);

			if (lists != null) {
				for (InventorySummary items : lists) {
					List<BigItemStack> entries = items.getStacksByCount();
					boolean cost = items == lists.getSecond();

					if (cost)
						tooltipComponents.add(Component.empty());

					if (entries.size() == 1) {
						BigItemStack entry = entries.get(0);
						(cost ? CreateLang.translate("table_cloth.total_cost") : CreateLang.text(""))
							.style(ChatFormatting.GOLD)
							.add(CreateLang.builder()
								.add(entry.stack.getHoverName().plainCopy())
								.text(" x")
								.text(String.valueOf(entry.count))
								.style(cost ? ChatFormatting.YELLOW : ChatFormatting.GRAY))
							.addTo(tooltipComponents);

					} else {
						if (cost)
							CreateLang.translate("table_cloth.total_cost")
								.style(ChatFormatting.GOLD)
								.addTo(tooltipComponents);
						for (BigItemStack entry : entries) {
							CreateLang.builder()
								.add(entry.stack.getHoverName().plainCopy())
								.text(" x")
								.text(String.valueOf(entry.count))
								.style(cost ? ChatFormatting.YELLOW : ChatFormatting.GRAY)
								.addTo(tooltipComponents);
						}
					}
				}
			}
		}

		CreateLang.translate("table_cloth.hand_to_shop_keeper")
			.style(ChatFormatting.GRAY)
			.addTo(tooltipComponents);

		CreateLang.translate("table_cloth.sneak_click_discard")
			.style(ChatFormatting.DARK_GRAY)
			.addTo(tooltipComponents);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		if (pUsedHand == InteractionHand.OFF_HAND || pPlayer == null || !pPlayer.isShiftKeyDown())
			return new InteractionResultHolder<>(InteractionResult.PASS, pPlayer.getItemInHand(pUsedHand));

		CreateLang.translate("table_cloth.shopping_list_discarded")
			.sendStatus(pPlayer);
		pPlayer.playSound(SoundEvents.BOOK_PAGE_TURN);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionHand pUsedHand = pContext.getHand();
		Player pPlayer = pContext.getPlayer();
		if (pUsedHand == InteractionHand.OFF_HAND || pPlayer == null || !pPlayer.isShiftKeyDown())
			return InteractionResult.PASS;
		pPlayer.setItemInHand(pUsedHand, ItemStack.EMPTY);

		CreateLang.translate("table_cloth.shopping_list_discarded")
			.sendStatus(pPlayer);
		pPlayer.playSound(SoundEvents.BOOK_PAGE_TURN);
		return InteractionResult.SUCCESS;
	}
}

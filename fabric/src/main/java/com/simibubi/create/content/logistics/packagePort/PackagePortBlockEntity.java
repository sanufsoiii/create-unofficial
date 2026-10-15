package com.simibubi.create.content.logistics.packagePort;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardContent;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.equipment.clipboard.ClipboardType;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PackagePortBlockEntity extends SmartBlockEntity implements MenuProvider, Clearable {
	public boolean acceptsPackages;
	public String addressFilter;
	public SmartInventory inventory;
	public PackagePortTarget target;

	public void filterChanged() {
		if (target != null) {
			target.deregister(this, level, worldPosition);
			target.register(this, level, worldPosition);
		}
	}

	protected AnimatedContainerBehaviour<?> openTracker;

	public PackagePortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		addressFilter = "";
		acceptsPackages = true;
		inventory = new SmartInventory(18, this, 64, false);
	}

	public boolean isBackedUp() {
		for (int i = 0; i < inventory.getSlotCount(); i++)
			if (inventory.getStackInSlot(i).isEmpty())
				return false;
		return true;
	}

	public String getFilterString() {
		return acceptsPackages ? addressFilter : null;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putString("AddressFilter", addressFilter);
		tag.putBoolean("AcceptsPackages", acceptsPackages);
		tag.put("Inventory", inventory.serializeNBT(registries));
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
		addressFilter = tag.getString("AddressFilter");
		acceptsPackages = tag.getBoolean("AcceptsPackages");
	}

	@Override
	public void clearContent() {
		inventory.clearContent();
	}

	@Override
	public void destroy() {
		super.destroy();
		for (int i = 0; i < inventory.getSlotCount(); i++)
			drop(inventory.getStackInSlot(i));
	}

	public void drop(ItemStack box) {
		if (box.isEmpty())
			return;
		Block.popResource(level, worldPosition, box);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	protected abstract void onOpenChange(boolean open);

	public ItemInteractionResult use(Player player) {
		if (player == null || player.isCrouching())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		if (player instanceof FakePlayer)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		ItemStack mainHandItem = player.getMainHandItem();
		boolean clipboard = mainHandItem.getItem().getDescriptionId().contains("clipboard");

		if (level.isClientSide) {
			if (!clipboard)
				onOpenedManually();
			return ItemInteractionResult.SUCCESS;
		}

		if (clipboard) {
			addAddressToClipboard(player, mainHandItem);
			return ItemInteractionResult.SUCCESS;
		}

		if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
			dev.architectury.registry.menu.MenuRegistry.openExtendedMenu(serverPlayer, this, buf -> buf.writeBlockPos(worldPosition));
		}

		return ItemInteractionResult.SUCCESS;
	}

	protected void onOpenedManually() {
	}

	private void addAddressToClipboard(Player player, ItemStack mainHandItem) {
		if (addressFilter == null || addressFilter.isBlank())
			return;

		ClipboardContent clipboard = mainHandItem.getOrDefault(AllDataComponents.CLIPBOARD_CONTENT, ClipboardContent.EMPTY);
		List<List<ClipboardEntry>> list = ClipboardEntry.readAll(clipboard);
		for (List<ClipboardEntry> page : list) {
			for (ClipboardEntry entry : page) {
				String existing = entry.text.getString();
				if (existing.equals("#" + addressFilter) || existing.equals("# " + addressFilter))
					return;
			}
		}

		List<ClipboardEntry> page = null;

		for (List<ClipboardEntry> freePage : list) {
			if (freePage.size() > 11)
				continue;
			page = freePage;
			break;
		}

		if (page == null) {
			page = new ArrayList<>();
			list.add(page);
		}

		page.add(new ClipboardEntry(false, Component.literal("#" + addressFilter)));
		player.displayClientMessage(CreateLang.translate("clipboard.address_added", addressFilter)
			.component(), true);

		clipboard = clipboard.setPages(list).setType(ClipboardType.WRITTEN);
		mainHandItem.set(AllDataComponents.CLIPBOARD_CONTENT, clipboard);
	}

	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return null;
	}

	public int getComparatorOutput() {
		return ItemHelper.calcRedstoneFromInventory(inventory);
	}
}

package com.simibubi.create.content.logistics.packager;

import java.util.LinkedList;
import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;

import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

public class PackagerBlockEntity extends SmartBlockEntity implements Clearable {
	public boolean redstonePowered;
	public int buttonCooldown;
	public String signBasedAddress;

	public ItemStack heldBox;
	public ItemStack previouslyUnwrapped;

	public InvManipulationBehaviour targetInventory;
	public List<BigItemStack> queuedExitingPackages;
	public final PackagerItemHandler inventory;

	public static final int CYCLE = 20;
	public int animationTicks;
	public boolean animationInward;

	private AdvancementBehaviour advancements;

	public PackagerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		redstonePowered = state.getOptionalValue(PackagerBlock.POWERED).orElse(false);
		heldBox = ItemStack.EMPTY;
		previouslyUnwrapped = ItemStack.EMPTY;
		inventory = new PackagerItemHandler(this);
		animationTicks = 0;
		animationInward = true;
		queuedExitingPackages = new LinkedList<>();
		signBasedAddress = "";
		buttonCooldown = 0;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(targetInventory = new InvManipulationBehaviour(this, InvManipulationBehaviour.InterfaceProvider.oppositeOfBlockFacing()));
		behaviours.add(advancements = new AdvancementBehaviour(this));
	}

	public boolean isTooBusyFor(com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType type) {
		int queue = queuedExitingPackages.size();
		return queue >= switch (type) {
			case PLAYER -> 50;
			case REDSTONE -> 20;
			case RESTOCK -> 10;
		};
	}

	public InventorySummary getAvailableItems() {
		return InventorySummary.EMPTY;
	}

	public void flashLink() {
	}

	public void triggerStockCheck() {
	}

	public void attemptToSend(List<PackagingRequest> requests) {
	}

	@Override
	public void tick() {
		super.tick();

		if (buttonCooldown > 0)
			buttonCooldown--;

		if (animationTicks == 0) {
			previouslyUnwrapped = ItemStack.EMPTY;

			if (!level.isClientSide() && !queuedExitingPackages.isEmpty() && heldBox.isEmpty()) {
				BigItemStack entry = queuedExitingPackages.get(0);
				heldBox = entry.stack.copy();

				entry.count--;
				if (entry.count <= 0)
					queuedExitingPackages.remove(0);

				animationInward = false;
				animationTicks = CYCLE;
				notifyUpdate();
			}

			return;
		}

		if (level.isClientSide) {
			if (animationTicks == CYCLE - (animationInward ? 5 : 1))
				AllSoundEvents.PACKAGER.playAt(level, worldPosition, 1, 1, true);
			if (animationTicks == (animationInward ? 1 : 5))
				level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
					true);
		}

		animationTicks--;

		if (animationTicks == 0 && !level.isClientSide()) {
			setChanged();
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide())
			return;
		recheckIfLinksPresent();
		if (!redstonePowered)
			return;
		redstonePowered = getBlockState().getOptionalValue(PackagerBlock.POWERED).orElse(false);
	}

	public void recheckIfLinksPresent() {
	}

	public boolean redstoneModeActive() {
		return !getBlockState().getOptionalValue(PackagerBlock.LINKED).orElse(false);
	}

	public void activate() {
		redstonePowered = true;
		setChanged();

		if (!redstoneModeActive())
			return;

		if (buttonCooldown <= 0) {
			buttonCooldown = 40;
		}
	}

	public boolean unwrapBox(ItemStack box, boolean simulate) {
		if (animationTicks > 0)
			return false;

		if (!simulate) {
			previouslyUnwrapped = box;
			animationInward = true;
			animationTicks = CYCLE;
			notifyUpdate();
		}

		return true;
	}

	protected String getSign(Direction side) {
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
		if (!(blockEntity instanceof SignBlockEntity sign))
			return null;
		for (boolean front : Iterate.trueAndFalse) {
			SignText text = sign.getText(front);
			String address = "";
			for (Component component : text.getMessages(false)) {
				String string = component.getString();
				if (!string.isBlank())
					address += string.trim() + " ";
			}
			if (!address.isBlank())
				return address.trim();
		}
		return null;
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		redstonePowered = compound.getBoolean("Active");
		animationInward = compound.getBoolean("AnimationInward");
		animationTicks = compound.getInt("AnimationTicks");
		signBasedAddress = compound.getString("SignAddress");
		heldBox = ItemStack.parseOptional(registries, compound.getCompound("HeldBox"));
		previouslyUnwrapped = ItemStack.parseOptional(registries, compound.getCompound("InsertedBox"));
		if (clientPacket)
			return;
		queuedExitingPackages = NBTHelper.readCompoundList(compound.getList("QueuedExitingPackages", Tag.TAG_COMPOUND),
			c -> CatnipCodecUtils.decode(BigItemStack.CODEC, registries, c)
				.orElseThrow());
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		compound.putBoolean("Active", redstonePowered);
		compound.putBoolean("AnimationInward", animationInward);
		compound.putInt("AnimationTicks", animationTicks);
		compound.putString("SignAddress", signBasedAddress);
		compound.put("HeldBox", heldBox.saveOptional(registries));
		compound.put("InsertedBox", previouslyUnwrapped.saveOptional(registries));
		if (clientPacket)
			return;
		compound.put("QueuedExitingPackages", NBTHelper.writeCompoundList(queuedExitingPackages, bis -> {
			if (CatnipCodecUtils.encode(BigItemStack.CODEC, registries, bis)
				.orElse(new CompoundTag()) instanceof CompoundTag ct)
				return ct;
			return new CompoundTag();
		}));
	}

	@Override
	public void clearContent() {
		inventory.setStackInSlot(0, ItemStack.EMPTY);
		queuedExitingPackages.clear();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inventory);
		queuedExitingPackages.forEach(bigStack -> {
			for (int i = 0; i < bigStack.count; i++)
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
					bigStack.stack.copy());
		});
		queuedExitingPackages.clear();
	}

	public float getTrayOffset(float partialTicks) {
		float tickCycle = animationInward ? animationTicks - partialTicks : animationTicks - 5 - partialTicks;
		float progress = Mth.clamp(tickCycle / (CYCLE - 5) * 2 - 1, -1, 1);
		progress = 1 - progress * progress;
		return progress * progress;
	}

	public ItemStack getRenderedBox() {
		if (animationInward)
			return animationTicks <= CYCLE / 2 ? ItemStack.EMPTY : previouslyUnwrapped;
		return animationTicks >= CYCLE / 2 ? ItemStack.EMPTY : heldBox;
	}
}

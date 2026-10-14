package com.simibubi.create.content.logistics.packagePort.frogport;

import java.util.List;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FrogportBlockEntity extends PackagePortBlockEntity implements IHaveHoveringInformation {

	public ItemStack animatedPackage;
	public LerpedFloat manualOpenAnimationProgress;
	public LerpedFloat animationProgress;
	public LerpedFloat anticipationProgress;
	public boolean currentlyDepositing;
	public boolean goggles;

	public boolean sendAnticipate;

	public float passiveYaw;

	private boolean failedLastExport;
	private FrogportSounds sounds;

	private ItemStack deferAnimationStart;
	private boolean deferAnimationInward;

	private AdvancementBehaviour advancements;

	public FrogportBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		sounds = new FrogportSounds();
		animationProgress = LerpedFloat.linear();
		anticipationProgress = LerpedFloat.linear();
		manualOpenAnimationProgress = LerpedFloat.linear()
			.startWithValue(0)
			.chase(0, 0.35, Chaser.LINEAR);
		goggles = false;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(advancements = new AdvancementBehaviour(this));
		super.addBehaviours(behaviours);
	}

	public boolean isAnimationInProgress() {
		return animationProgress.getChaseTarget() == 1;
	}

	@Override
	public AABB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expandTowards(0, 1, 0);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide() || isAnimationInProgress())
			return;

		boolean prevFail = failedLastExport;
		if (failedLastExport != prevFail)
			sendData();
	}

	public void sendAnticipate() {
		if (isAnimationInProgress())
			return;
		for (int i = 0; i < inventory.getSlotCount(); i++)
			if (inventory.getStackInSlot(i).isEmpty()) {
				sendAnticipate = true;
				sendData();
				return;
			}
	}

	public void anticipate() {
		anticipationProgress.chase(1, 0.1, Chaser.LINEAR);
	}

	@Override
	public void tick() {
		super.tick();

		if (deferAnimationStart != null) {
			startAnimation(deferAnimationStart, deferAnimationInward);
			deferAnimationStart = null;
		}

		if (anticipationProgress.getValue() == 1)
			anticipationProgress.startWithValue(0);

		manualOpenAnimationProgress.updateChaseTarget(0);
		boolean wasOpen = manualOpenAnimationProgress.getValue() > 0;

		anticipationProgress.tickChaser();
		manualOpenAnimationProgress.tickChaser();

		if (level.isClientSide() && wasOpen && manualOpenAnimationProgress.getValue() == 0)
			sounds.close(level, worldPosition);

		if (!isAnimationInProgress())
			return;

		animationProgress.tickChaser();

		float value = animationProgress.getValue();
		if (currentlyDepositing) {
			if (!level.isClientSide() || isVirtual()) {
				if (value > 0.5 && animatedPackage != null) {
					drop(animatedPackage);
					animatedPackage = null;
				}
			} else {
				if (value > 0.7 && animatedPackage != null)
					animatedPackage = null;
			}
		}

		if (value < 1)
			return;

		anticipationProgress.startWithValue(0);
		animationProgress.startWithValue(0);
		if (level.isClientSide()) {
			animatedPackage = null;
			return;
		}

		if (!currentlyDepositing && animatedPackage != null) {
			drop(animatedPackage);
		}

		animatedPackage = null;
	}

	public void startAnimation(ItemStack box, boolean deposit) {
		if (!PackageItem.isPackage(box))
			return;

		animationProgress.startWithValue(0);
		animationProgress.chase(1, 0.1, Chaser.LINEAR);
		animatedPackage = box;
		currentlyDepositing = deposit;

		// if (level != null && !deposit && !level.isClientSide())
		// 	advancements.awardPlayer(AllAdvancements.FROGPORT);

		if (level != null && level.isClientSide()) {
			sounds.open(level, worldPosition);
			if (currentlyDepositing) {
				sounds.depositPackage(level, worldPosition);
			} else {
				sounds.catchPackage(level, worldPosition);
			}
		}

		if (level != null && !level.isClientSide()) {
			level.blockEntityChanged(worldPosition);
			sendData();
		}
	}

	@Override
	protected void onOpenChange(boolean open) {
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putFloat("PlacedYaw", passiveYaw);
		if (animatedPackage != null && isAnimationInProgress()) {
			tag.put("AnimatedPackage", animatedPackage.saveOptional(registries));
			tag.putBoolean("Deposit", currentlyDepositing);
		}
		if (sendAnticipate) {
			sendAnticipate = false;
			tag.putBoolean("Anticipate", true);
		}
		if (failedLastExport)
			NBTHelper.putMarker(tag, "FailedLastExport");
		if (goggles)
			NBTHelper.putMarker(tag, "Goggles");
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		passiveYaw = tag.getFloat("PlacedYaw");
		failedLastExport = tag.getBoolean("FailedLastExport");
		goggles = tag.getBoolean("Goggles");
		if (!clientPacket)
			animatedPackage = null;
		if (tag.contains("AnimatedPackage")) {
			deferAnimationInward = tag.getBoolean("Deposit");
			deferAnimationStart = ItemStack.parseOptional(registries, tag.getCompound("AnimatedPackage"));
		}
		if (clientPacket && tag.contains("Anticipate"))
			anticipate();
	}

	public float getYaw() {
		return passiveYaw;
	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean superTip = IHaveHoveringInformation.super.addToTooltip(tooltip, isPlayerSneaking);
		if (!failedLastExport)
			return superTip;
		TooltipHelper.addHint(tooltip, "hint.blocked_frogport");
		return true;
	}

	@Override
	protected void onOpenedManually() {
		if (level.isClientSide())
			sounds.open(level, worldPosition);
	}

	@Override
	public ItemInteractionResult use(Player player) {
		if (player == null)
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		ItemStack mainHandItem = player.getMainHandItem();
		if (!goggles && AllItems.GOGGLES.isIn(mainHandItem)) {
			goggles = true;
			if (!level.isClientSide()) {
				notifyUpdate();
				level.playSound(null, worldPosition, SoundEvents.ARMOR_EQUIP_GOLD.value(), SoundSource.BLOCKS, 0.5f, 1.0f);
			}
			return ItemInteractionResult.SUCCESS;
		}

		return super.use(player);
	}

}

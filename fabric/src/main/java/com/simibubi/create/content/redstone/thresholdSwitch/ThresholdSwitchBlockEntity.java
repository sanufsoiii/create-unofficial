package com.simibubi.create.content.redstone.thresholdSwitch;

import java.util.List;

import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.redstone.DirectedDirectionalBlock;
import com.simibubi.create.content.redstone.FilteredDetectorFilterSlot;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase.InterfaceProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemTransferUtil;
import com.simibubi.create.foundation.utility.CreateLang;

import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.createmod.catnip.math.BlockFace;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

public class ThresholdSwitchBlockEntity extends SmartBlockEntity implements Clearable {
	public int onWhenAbove;
	public int offWhenBelow;

	public int currentMinLevel;
	public int currentLevel;
	public int currentMaxLevel;
	public boolean inStacks;

	private boolean redstoneState;
	private boolean inverted;
	private boolean poweredAfterDelay;

	private FilteringBehaviour filtering;
	private InvManipulationBehaviour observedInventory;
	private TankManipulationBehaviour observedTank;
	private VersionedInventoryTrackerBehaviour invVersionTracker;

	public ThresholdSwitchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		onWhenAbove = 128;
		offWhenBelow = 64;
		currentLevel = -1;
		redstoneState = false;
		inverted = false;
		poweredAfterDelay = false;
		setLazyTickRate(10);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		onWhenAbove = compound.getInt("OnAboveAmount");
		offWhenBelow = compound.getInt("OffBelowAmount");
		currentLevel = compound.getInt("CurrentAmount");
		currentMinLevel = compound.getInt("CurrentMinAmount");
		currentMaxLevel = compound.getInt("CurrentMaxAmount");
		inStacks = compound.getBoolean("InStacks");
		redstoneState = compound.getBoolean("Powered");
		inverted = compound.getBoolean("Inverted");
		poweredAfterDelay = compound.getBoolean("PoweredAfterDelay");
		super.read(compound, registries, clientPacket);
	}

	protected void writeCommon(CompoundTag compound) {
		compound.putFloat("OnAboveAmount", onWhenAbove);
		compound.putFloat("OffBelowAmount", offWhenBelow);
		compound.putBoolean("Inverted", inverted);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		writeCommon(compound);
		compound.putInt("CurrentAmount", currentLevel);
		compound.putInt("CurrentMinAmount", currentMinLevel);
		compound.putInt("CurrentMaxAmount", currentMaxLevel);
		compound.putBoolean("InStacks", inStacks);
		compound.putBoolean("Powered", redstoneState);
		compound.putBoolean("PoweredAfterDelay", poweredAfterDelay);
		super.write(compound, registries, clientPacket);
	}

	@Override
	public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
		writeCommon(compound);
		super.writeSafe(compound, registries);
	}

	public int getMinLevel() {
		return currentMinLevel;
	}

	public int getStockLevel() {
		return currentLevel;
	}

	public int getMaxLevel() {
		return currentMaxLevel;
	}

	public void updateCurrentLevel() {
		boolean changed = false;
		int prevLevel = currentLevel;
		int prevMaxLevel = currentMaxLevel;

		BlockPos target = getTargetPos();
		BlockEntity targetBlockEntity = level.getBlockEntity(target);

		observedInventory.findNewCapability();
		observedTank.findNewCapability();

		if (targetBlockEntity instanceof ThresholdSwitchObservable observable) {
			currentMinLevel = observable.getMinValue();
			currentLevel = observable.getCurrentValue();
			currentMaxLevel = observable.getMaxValue();

		} else if (observedInventory.hasInventory() || observedTank.hasInventory()) {
			currentMinLevel = 0;
			currentLevel = 0;
			currentMaxLevel = 0;

			if (observedInventory.hasInventory()) {
				Object invObj = observedInventory.getInventory();
				if (invObj instanceof Storage<?> storage) {
					@SuppressWarnings("unchecked")
					Storage<ItemVariant> inv = (Storage<ItemVariant>) storage;
					if (invVersionTracker.stillWaiting(inv)) {
						currentLevel = prevLevel;
						currentMaxLevel = prevMaxLevel;
					} else {
						invVersionTracker.awaitNewVersion(inv);
						for (StorageView<ItemVariant> view : inv) {
							long space = view.getCapacity();
							long count = view.getAmount();
							if (space <= 0)
								continue;

							currentMaxLevel += space;
							if (!view.isResourceBlank() && filtering.test(view.getResource().toStack((int) Math.min(count, 64))))
								currentLevel += count;
						}
					}
				} else if (invObj instanceof Container container) {
					for (int slot = 0; slot < container.getContainerSize(); slot++) {
						ItemStack stackInSlot = container.getItem(slot);
						long space = Math.min(stackInSlot.getOrDefault(DataComponents.MAX_STACK_SIZE, 64), container.getMaxStackSize());
						int count = stackInSlot.getCount();
						if (space <= 0)
							continue;

						currentMaxLevel += space;
						if (filtering.test(stackInSlot))
							currentLevel += count;
					}
				}
			}

			if (observedTank.hasInventory()) {
				Storage<FluidVariant> tank = observedTank.getInventory();
				if (tank != null) {
					for (StorageView<FluidVariant> view : tank) {
						long space = view.getCapacity() / 81; // 81 droplets = 1 mB
						long count = view.getAmount() / 81;
						if (space <= 0)
							continue;

						currentMaxLevel += space;
						if (!view.isResourceBlank() && filtering.test(FluidStackHooksFabric.fromFabric(view.getResource(), view.getAmount())))
							currentLevel += count;
					}
				}
			}

		} else {
			// No compatible inventories found
			currentMinLevel = -1;
			currentMaxLevel = -1;
			if (currentLevel == -1)
				return;

			level.setBlock(worldPosition, getBlockState().setValue(ThresholdSwitchBlock.LEVEL, 0), Block.UPDATE_ALL);
			currentLevel = -1;
			redstoneState = false;
			sendData();
			scheduleBlockTick();
			return;
		}

		currentLevel = Mth.clamp(currentLevel, currentMinLevel, currentMaxLevel);
		changed = currentLevel != prevLevel;

		boolean previouslyPowered = redstoneState;
		if (redstoneState && currentLevel <= offWhenBelow)
			redstoneState = false;
		else if (!redstoneState && currentLevel >= onWhenAbove)
			redstoneState = true;
		boolean update = previouslyPowered != redstoneState;

		int displayLevel = 0;
		float normedLevel = (float) (currentLevel - currentMinLevel) / (currentMaxLevel - currentMinLevel);
		if (currentLevel > 0)
			displayLevel = (int) (1 + normedLevel * 4);
		level.setBlock(worldPosition, getBlockState().setValue(ThresholdSwitchBlock.LEVEL, displayLevel),
			update ? 3 : 2);

		if (update)
			scheduleBlockTick();

		if (changed || update) {
			notifyUpdate();
		}
	}

	private boolean isSuitableInventory(BlockEntity be) {
		return be != null && !"StockTickerBlockEntity".equals(be.getClass().getSimpleName())
			&& !(ItemTransferUtil.getInventory(level, be.getBlockPos(), null) instanceof ProcessingInventory);
	}

	private BlockPos getTargetPos() {
		return worldPosition.relative(ThresholdSwitchBlock.getTargetDirection(getBlockState()));
	}

	public ItemStack getDisplayItemForScreen() {
		BlockPos target = getTargetPos();
		return new ItemStack(level.getBlockState(target)
			.getBlock());
	}

	public static enum ThresholdType {
		UNSUPPORTED, ITEM, FLUID, CUSTOM;
	}

	public MutableComponent format(int value, boolean stacks) {
		ThresholdType type = getTypeOfCurrentTarget();
		if (type == ThresholdType.CUSTOM)
			if (level.getBlockEntity(getTargetPos()) instanceof ThresholdSwitchObservable tso)
				return tso.format(value);

		String suffix = type == ThresholdType.ITEM
			? stacks ? "schedule.condition.threshold.stacks" : "schedule.condition.threshold.items"
			: "schedule.condition.threshold.buckets";
		return CreateLang.text((type == ThresholdType.FLUID ? value / 1000 : value) + " ")
			.add(CreateLang.translate(suffix))
			.component();
	}

	public ThresholdType getTypeOfCurrentTarget() {
		if (observedInventory.hasInventory())
			return ThresholdType.ITEM;
		if (observedTank.hasInventory())
			return ThresholdType.FLUID;
		if (level.getBlockEntity(getTargetPos()) instanceof ThresholdSwitchObservable)
			return ThresholdType.CUSTOM;
		return ThresholdType.UNSUPPORTED;
	}

	protected void scheduleBlockTick() {
		Block block = getBlockState().getBlock();
		if (!level.getBlockTicks()
			.willTickThisTick(worldPosition, block))
			level.scheduleTick(worldPosition, block, 2, TickPriority.NORMAL);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (level.isClientSide)
			return;
		updateCurrentLevel();
	}

	@Override
	public void clearContent() {
		filtering.setFilter(ItemStack.EMPTY);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours
			.add(filtering = new FilteringBehaviour(this, new FilteredDetectorFilterSlot(true)).withCallback($ -> {
				this.updateCurrentLevel();
				invVersionTracker.reset();
			}));

		behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));

		InterfaceProvider towardBlockFacing =
			(w, p, s) -> new BlockFace(p, DirectedDirectionalBlock.getTargetDirection(s));

		behaviours.add(observedInventory = new InvManipulationBehaviour(this, towardBlockFacing).bypassSidedness()
			.withFilter(this::isSuitableInventory));
		behaviours.add(observedTank = new TankManipulationBehaviour(this, towardBlockFacing).bypassSidedness());
	}

	public float getLevelForDisplay() {
		return currentLevel == -1 ? 0 : currentLevel;
	}

	public boolean getState() {
		return redstoneState;
	}

	public boolean shouldBePowered() {
		return inverted != redstoneState;
	}

	public void updatePowerAfterDelay() {
		poweredAfterDelay = shouldBePowered();
		level.blockUpdated(worldPosition, getBlockState().getBlock());
		sendData();
	}

	public boolean isPowered() {
		return poweredAfterDelay;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		if (inverted == this.inverted)
			return;
		this.inverted = inverted;
		updatePowerAfterDelay();
	}
}

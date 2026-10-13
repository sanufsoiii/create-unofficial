package com.simibubi.create.content.logistics.chute;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.ItemHelper.ExtractionCountMode;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.StorageProvider;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChuteBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, Clearable, SidedStorageBlockEntity {

	float pull;
	float push;

	ItemStack item;
	LerpedFloat itemPosition;
	ChuteItemHandler itemHandler;
	boolean canPickUpItems;

	float bottomPullDistance;
	float beltBelowOffset;
	TransportedItemStackHandlerBehaviour beltBelow;
	boolean updateAirFlow;
	int airCurrentUpdateCooldown;
	int entitySearchCooldown;

	VersionedInventoryTrackerBehaviour invVersionTracker;

	StorageProvider<ItemVariant> capAbove;
	StorageProvider<ItemVariant> capBelow;

	public ChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		item = ItemStack.EMPTY;
		itemPosition = LerpedFloat.linear();
		itemHandler = new ChuteItemHandler(this);
		canPickUpItems = false;
		bottomPullDistance = 0;
		updateAirFlow = true;
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		capAbove = StorageProvider.createForItems(level, worldPosition.above());
		capBelow = StorageProvider.createForItems(level, worldPosition.below());
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen((d) -> canDirectlyInsertCached()));
		behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
	}

	public boolean canDirectlyInsertCached() {
		return canPickUpItems;
	}

	private boolean canDirectlyInsert() {
		BlockState blockState = getBlockState();
		BlockState blockStateAbove = level.getBlockState(worldPosition.above());
		if (!AbstractChuteBlock.isChute(blockState))
			return false;
		if (AbstractChuteBlock.getChuteFacing(blockStateAbove) == Direction.DOWN)
			return false;
		if (getItemMotion() > 0 && getInputChutes().isEmpty())
			return false;
		return AbstractChuteBlock.isOpenChute(blockState);
	}

	@Override
	public void initialize() {
		super.initialize();
		onAdded();
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -3, 0);
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide)
			canPickUpItems = canDirectlyInsert();

		boolean clientSide = level != null && level.isClientSide && !isVirtual();
		float itemMotion = getItemMotion();
		if (itemMotion != 0 && level != null && level.isClientSide)
			spawnParticles(itemMotion);
		tickAirStreams(itemMotion);

		if (item.isEmpty() && !clientSide) {
			if (itemMotion < 0)
				handleInputFromAbove();
			if (itemMotion > 0)
				handleInputFromBelow();
			return;
		}

		float nextOffset = itemPosition.getValue() + itemMotion;

		if (itemMotion < 0) {
			if (nextOffset < .5f) {
				if (!handleDownwardOutput(true))
					nextOffset = .5f;
				else if (nextOffset < 0) {
					handleDownwardOutput(clientSide);
					nextOffset = itemPosition.getValue();
				}
			}
		} else if (itemMotion > 0) {
			if (nextOffset > .5f) {
				if (!handleUpwardOutput(true))
					nextOffset = .5f;
				else if (nextOffset > 1) {
					handleUpwardOutput(clientSide);
					nextOffset = itemPosition.getValue();
				}
			}
		}

		itemPosition.setValue(nextOffset);
	}

	private void updateAirFlow(float itemSpeed) {
		updateAirFlow = false;
		if (itemSpeed > 0 && level != null && !level.isClientSide) {
			float speed = pull - push;
			beltBelow = null;

			float maxPullDistance;
			if (speed >= 128)
				maxPullDistance = 3;
			else if (speed >= 64)
				maxPullDistance = 2;
			else if (speed >= 32)
				maxPullDistance = 1;
			else
				maxPullDistance = Mth.lerp(speed / 32, 0, 1);

			if (AbstractChuteBlock.isChute(level.getBlockState(worldPosition.below())))
				maxPullDistance = 0;
			float flowLimit = maxPullDistance;
			if (flowLimit > 0)
				flowLimit = getFlowLimit(level, worldPosition, maxPullDistance, Direction.DOWN);

			for (int i = 1; i <= flowLimit + 1; i++) {
				TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get(level, worldPosition.below(i),
						TransportedItemStackHandlerBehaviour.TYPE);
				if (behaviour == null)
					continue;
				beltBelow = behaviour;
				beltBelowOffset = i - 1;
				break;
			}
			this.bottomPullDistance = Math.max(0, flowLimit);
		}
		sendData();
	}

	public static float getFlowLimit(Level world, BlockPos start, float max, Direction facing) {
		for (int i = 0; i < max; i++) {
			BlockPos currentPos = start.relative(facing, i + 1);
			if (!world.isLoaded(currentPos))
				return i;
			BlockState state = world.getBlockState(currentPos);
			if (state.isAir())
				continue;
			VoxelShape shape = state.getCollisionShape(world, currentPos);
			if (shape.isEmpty())
				continue;
			if (shape == Shapes.block())
				return i;
		}
		return max;
	}

	private void findEntities(float itemSpeed) {
		if (bottomPullDistance <= 0 && !getItem().isEmpty() || itemSpeed <= 0 || level == null || level.isClientSide)
			return;
		if (!canCollectItemsFromBelow())
			return;
		Vec3 center = VecHelper.getCenterOf(worldPosition);
		AABB searchArea = new AABB(center.add(0, -bottomPullDistance - 0.5, 0), center.add(0, -0.5, 0)).inflate(.45f);
		for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, searchArea)) {
			if (!itemEntity.isAlive())
				continue;
			ItemStack entityItem = itemEntity.getItem();
			if (!canAcceptItem(entityItem))
				continue;
			setItem(entityItem.copy(), (float) (itemEntity.getBoundingBox().getCenter().y - worldPosition.getY()));
			itemEntity.discard();
			break;
		}
	}

	private void extractFromBelt(float itemSpeed) {
		if (itemSpeed <= 0 || level == null || level.isClientSide)
			return;
		if (getItem().isEmpty() && beltBelow != null) {
			beltBelow.handleCenteredProcessingOnAllItems(.5f, ts -> {
				if (canAcceptItem(ts.stack)) {
					setItem(ts.stack.copy(), -beltBelowOffset);
					return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
				}
				return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
			});
		}
	}

	private void tickAirStreams(float itemSpeed) {
		if (level != null && level.isClientSide)
			return;
		if (airCurrentUpdateCooldown-- <= 0) {
			airCurrentUpdateCooldown = AllConfigs.server().kinetics.fanBlockCheckRate.get();
			updateAirFlow = true;
		}
		if (updateAirFlow) {
			updateAirFlow(itemSpeed);
		}
		if (entitySearchCooldown-- <= 0) {
			entitySearchCooldown = 5;
			findEntities(itemSpeed);
			extractFromBelt(itemSpeed);
		}
	}

	public void blockBelowChanged() {
		updateAirFlow = true;
	}

	private void spawnParticles(float itemMotion) {
		if (level == null)
			return;
		BlockState state = getBlockState();
		if (!AbstractChuteBlock.isChute(state))
			return;
		if (level.random.nextInt(4) != 0)
			return;
		Direction direction = AbstractChuteBlock.getChuteFacing(state);
		Vec3 center = VecHelper.getCenterOf(worldPosition);
		Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, level.random, .3f);
		level.addParticle(ParticleTypes.POOF, center.x + offset.x, center.y + offset.y, center.z + offset.z, 0, itemMotion * 0.5, 0);
	}

	private void handleInputFromAbove() {
		if (!canAcceptItem(item))
			return;
		Storage<ItemVariant> storage = grabCapability(Direction.UP);
		if (storage != null) {
			if (invVersionTracker.stillWaiting(storage))
				return;
			ItemStack extracted = ItemHelper.extract(storage, this::canAcceptItem, getExtractionMode(),
					getExtractionAmount(), false);
			if (!extracted.isEmpty()) {
				setItem(extracted, 1);
				return;
			}
			invVersionTracker.awaitNewVersion(storage);
		}
	}

	private void handleInputFromBelow() {
		if (!canAcceptItem(item))
			return;
		Storage<ItemVariant> storage = grabCapability(Direction.DOWN);
		if (storage != null) {
			if (invVersionTracker.stillWaiting(storage))
				return;
			ItemStack extracted = ItemHelper.extract(storage, this::canAcceptItem, getExtractionMode(),
					getExtractionAmount(), false);
			if (!extracted.isEmpty()) {
				setItem(extracted, 0);
				return;
			}
			invVersionTracker.awaitNewVersion(storage);
		}
	}

	private boolean handleDownwardOutput(boolean simulate) {
		BlockState blockState = getBlockState();
		ChuteBlockEntity targetChute = getTargetChute(blockState);
		Direction direction = AbstractChuteBlock.getChuteFacing(blockState);

		if (level == null || direction == null || !this.canOutputItems())
			return false;
		Storage<ItemVariant> inv = grabCapability(Direction.DOWN);
		if (inv != null) {
			if (level.isClientSide && !isVirtual())
				return false;

			if (invVersionTracker.stillWaiting(inv))
				return false;

			try (Transaction t = TransferUtil.getTransaction()) {
				long inserted = inv.insert(ItemVariant.of(item), item.getCount(), t);
				if (inserted != 0 && !simulate)
					t.commit();
				ItemStack held = getItem();
				if (!simulate) {
					ItemStack newStack = held.copy();
					newStack.shrink(ItemHelper.truncateLong(inserted));
					setItem(newStack, itemPosition.getValue(0));
				}
				if (inserted != 0)
					return true;
			}

			invVersionTracker.awaitNewVersion(inv);
			if (direction == Direction.DOWN)
				return false;
		}

		if (targetChute != null) {
			boolean canInsert = targetChute.canAcceptItem(item);
			if (!simulate && canInsert) {
				targetChute.setItem(item, direction == Direction.DOWN ? 1 : .51f);
				setItem(ItemStack.EMPTY);
			}
			return canInsert;
		}

		if (direction.getAxis().isHorizontal())
			return false;

		BlockState stateBelow = level.getBlockState(worldPosition.below());
		if (stateBelow.getBlock().getClass().getSimpleName().equals("FunnelBlock"))
			return false;
		if (Block.canSupportRigidBlock(level, worldPosition.below()))
			return false;

		if (!simulate) {
			Vec3 dropVec = VecHelper.getCenterOf(worldPosition).add(0, -12 / 16f, 0);
			ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, item.copy());
			dropped.setDefaultPickUpDelay();
			dropped.setDeltaMovement(0, -.25f, 0);
			level.addFreshEntity(dropped);
			setItem(ItemStack.EMPTY);
		}

		return true;
	}

	private boolean handleUpwardOutput(boolean simulate) {
		BlockState stateAbove = level.getBlockState(worldPosition.above());
		ChuteBlockEntity bestOutput = null;
		List<ChuteBlockEntity> inputChutes = getInputChutes();
		for (ChuteBlockEntity inputChute : inputChutes) {
			if (!inputChute.canAcceptItem(item))
				continue;
			bestOutput = inputChute;
			break;
		}

		if (bestOutput != null) {
			if (!simulate) {
				bestOutput.setItem(item, 0);
				setItem(ItemStack.EMPTY);
			}
			return true;
		}

		BlockState blockState = getBlockState();
		if (stateAbove.getBlock().getClass().getSimpleName().equals("FunnelBlock"))
			return false;
		if (BlockHelper.hasBlockSolidSide(stateAbove, level, worldPosition.above(), Direction.DOWN))
			return false;
		if (!inputChutes.isEmpty())
			return false;

		Storage<ItemVariant> inv = grabCapability(Direction.UP);
		if (inv != null) {
			if (level.isClientSide && !isVirtual())
				return false;
			if (invVersionTracker.stillWaiting(inv))
				return false;

			try (Transaction t = TransferUtil.getTransaction()) {
				long inserted = inv.insert(ItemVariant.of(item), item.getCount(), t);
				if (inserted != 0 && !simulate)
					t.commit();
				ItemStack held = getItem();
				if (!simulate) {
					ItemStack newStack = held.copy();
					newStack.shrink(ItemHelper.truncateLong(inserted));
					setItem(newStack, itemPosition.getValue(0));
				}
				if (inserted != 0)
					return true;
			}

			invVersionTracker.awaitNewVersion(inv);
			return false;
		}

		if (!simulate) {
			Vec3 dropVec = VecHelper.getCenterOf(worldPosition).add(0, 8 / 16f, 0);
			ItemEntity dropped = new ItemEntity(level, dropVec.x, dropVec.y, dropVec.z, item.copy());
			dropped.setDefaultPickUpDelay();
			dropped.setDeltaMovement(0, getItemMotion() * 2, 0);
			level.addFreshEntity(dropped);
			setItem(ItemStack.EMPTY);
		}
		return true;
	}

	protected boolean canAcceptItem(ItemStack stack) {
		return item.isEmpty();
	}

	protected int getExtractionAmount() {
		return 16;
	}

	protected ExtractionCountMode getExtractionMode() {
		return ExtractionCountMode.UPTO;
	}

	protected boolean canCollectItemsFromBelow() {
		return true;
	}

	protected boolean canOutputItems() {
		return true;
	}

	@Nullable
	private Storage<ItemVariant> grabCapability(Direction side) {
		if (level == null)
			return null;
		StorageProvider<ItemVariant> provider = side == Direction.UP ? capAbove : capBelow;
		BlockEntity be = provider.findBlockEntity();
		if (be instanceof ChuteBlockEntity) {
			if (side != Direction.DOWN || !(be instanceof SmartChuteBlockEntity) || getItemMotion() > 0)
				return null;
		}
		return provider.get(side.getOpposite());
	}

	public void setItem(ItemStack stack) {
		setItem(stack, getItemMotion() < 0 ? 1 : 0);
	}

	public void setItem(ItemStack stack, float insertionPos) {
		item = stack;
		itemPosition.startWithValue(insertionPos);
		itemHandler.update();
		invVersionTracker.reset();
		if (!level.isClientSide) {
			notifyUpdate();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.put("Item", item.saveOptional(registries));
		compound.putFloat("ItemPosition", itemPosition.getValue());
		compound.putFloat("Pull", pull);
		compound.putFloat("Push", push);
		compound.putFloat("BottomAirFlowDistance", bottomPullDistance);
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		ItemStack previousItem = item;
		item = ItemStack.parseOptional(registries, compound.getCompound("Item"));
		itemHandler.update();
		itemPosition.startWithValue(compound.getFloat("ItemPosition"));
		pull = compound.getFloat("Pull");
		push = compound.getFloat("Push");
		bottomPullDistance = compound.getFloat("BottomAirFlowDistance");
		super.read(compound, registries, clientPacket);

		if (hasLevel() && level != null && level.isClientSide && !ItemStack.matches(previousItem, item)
				&& !item.isEmpty()) {
			if (level.random.nextInt(3) != 0)
				return;
			Vec3 p = VecHelper.getCenterOf(worldPosition);
			p = VecHelper.offsetRandomly(p, level.random, .5f);
			Vec3 m = Vec3.ZERO;
			level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), p.x, p.y, p.z, m.x, m.y, m.z);
		}
	}

	public float getItemMotion() {
		final float fanSpeedModifier = 1 / 64f;
		final float maxItemSpeed = 20f;
		final float gravity = 4f;

		float motion = (push + pull) * fanSpeedModifier;
		return (Mth.clamp(motion, -maxItemSpeed, maxItemSpeed) + (motion <= 0 ? -gravity : 0)) / 20f;
	}

	@Override
	public void destroy() {
		super.destroy();
		ChuteBlockEntity targetChute = getTargetChute(getBlockState());
		List<ChuteBlockEntity> inputChutes = getInputChutes();
		if (!item.isEmpty() && level != null)
			Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), item);
		setRemoved();
		if (targetChute != null) {
			targetChute.updatePull();
			targetChute.propagatePush();
		}
		inputChutes.forEach(c -> c.updatePush(inputChutes.size()));
	}

	public void onAdded() {
		refreshBlockState();
		updatePull();
		ChuteBlockEntity targetChute = getTargetChute(getBlockState());
		if (targetChute != null)
			targetChute.propagatePush();
		else
			updatePush(1);
	}

	public void updatePull() {
		float totalPull = calculatePull();
		if (pull == totalPull)
			return;
		pull = totalPull;
		updateAirFlow = true;
		sendData();
		ChuteBlockEntity targetChute = getTargetChute(getBlockState());
		if (targetChute != null)
			targetChute.updatePull();
	}

	public void updatePush(int branchCount) {
		float totalPush = calculatePush(branchCount);
		if (push == totalPush)
			return;
		push = totalPush;
		updateAirFlow = true;
		sendData();
		propagatePush();
	}

	public void propagatePush() {
		List<ChuteBlockEntity> inputs = getInputChutes();
		inputs.forEach(c -> c.updatePush(inputs.size()));
	}

	protected float calculatePull() {
		BlockState blockStateAbove = level.getBlockState(worldPosition.above());
		if (blockStateAbove.getBlock().getClass().getSimpleName().equals("EncasedFanBlock")
				&& blockStateAbove.getValue(DirectionalKineticBlock.FACING) == Direction.DOWN) {
			BlockEntity be = level.getBlockEntity(worldPosition.above());
			if (be instanceof KineticBlockEntity kbe && !be.isRemoved()) {
				return kbe.getSpeed();
			}
		}

		float totalPull = 0;
		for (Direction d : Iterate.directions) {
			ChuteBlockEntity inputChute = getInputChute(d);
			if (inputChute == null)
				continue;
			totalPull += inputChute.pull;
		}
		return totalPull;
	}

	protected float calculatePush(int branchCount) {
		if (level == null)
			return 0;
		BlockState blockStateBelow = level.getBlockState(worldPosition.below());
		if (blockStateBelow.getBlock().getClass().getSimpleName().equals("EncasedFanBlock")
				&& blockStateBelow.getValue(DirectionalKineticBlock.FACING) == Direction.UP) {
			BlockEntity be = level.getBlockEntity(worldPosition.below());
			if (be instanceof KineticBlockEntity kbe && !be.isRemoved()) {
				return kbe.getSpeed();
			}
		}

		ChuteBlockEntity targetChute = getTargetChute(getBlockState());
		if (targetChute == null)
			return 0;
		return targetChute.push / branchCount;
	}

	@Nullable
	private ChuteBlockEntity getTargetChute(BlockState state) {
		if (level == null)
			return null;
		if (!AbstractChuteBlock.isChute(state))
			return null;
		Direction targetDirection = AbstractChuteBlock.getChuteFacing(state);
		if (targetDirection == null)
			return null;
		BlockPos targetPos = worldPosition.below();
		if (targetDirection.getAxis().isHorizontal())
			targetPos = targetPos.relative(targetDirection.getOpposite());
		BlockEntity be = level.getBlockEntity(targetPos);
		if (be instanceof ChuteBlockEntity && !be.isRemoved())
			return (ChuteBlockEntity) be;
		return null;
	}

	private List<ChuteBlockEntity> getInputChutes() {
		List<ChuteBlockEntity> inputs = new LinkedList<>();
		for (Direction d : Iterate.directions) {
			ChuteBlockEntity inputChute = getInputChute(d);
			if (inputChute == null)
				continue;
			inputs.add(inputChute);
		}
		return inputs;
	}

	@Nullable
	private ChuteBlockEntity getInputChute(Direction direction) {
		if (level == null || direction == Direction.DOWN)
			return null;
		direction = direction.getOpposite();
		BlockPos chutePos = worldPosition.above();
		if (direction.getAxis().isHorizontal())
			chutePos = chutePos.relative(direction);
		BlockState chuteState = level.getBlockState(chutePos);
		Direction chuteFacing = AbstractChuteBlock.getChuteFacing(chuteState);
		if (chuteFacing != direction)
			return null;
		BlockEntity be = level.getBlockEntity(chutePos);
		if (be instanceof ChuteBlockEntity && !be.isRemoved())
			return (ChuteBlockEntity) be;
		return null;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		CreateLang.translate("block.chute.goggles.title")
			.forGoggles(tooltip);
		return true;
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		return itemHandler;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public void clearContent() {
		item = ItemStack.EMPTY;
		itemHandler.update();
	}

}

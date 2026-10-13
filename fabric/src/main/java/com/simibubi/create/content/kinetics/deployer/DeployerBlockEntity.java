package com.simibubi.create.content.kinetics.deployer;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DeployerBlockEntity extends KineticBlockEntity implements SidedStorageBlockEntity {

	public State state;
	public Mode mode;
	protected ItemStack heldItem = ItemStack.EMPTY;
	protected DeployerFakePlayer player;
	protected int timer;
	protected float reach;
	protected boolean fistBump = false;
	protected List<ItemStack> overflowItems = new ArrayList<>();
	protected FilteringBehaviour filtering;
	public boolean redstoneLocked;
	public UUID owner;
	private DeployerItemHandler invHandler;
	private ListTag deferredInventoryList;

	private LerpedFloat animatedOffset;

	public BeltProcessingBehaviour processingBehaviour;

	// list holds the held item in index 0, followed by overflow items
	public SnapshotParticipant<List<ItemStack>> snapshotParticipant = new SnapshotParticipant<>() {
		@Override
		protected List<ItemStack> createSnapshot() {
			List<ItemStack> stacks = new ArrayList<>();
			stacks.add(player == null ? ItemStack.EMPTY : player.getMainHandItem());
			stacks.addAll(overflowItems);
			return stacks;
		}

		@Override
		protected void readSnapshot(List<ItemStack> snapshot) {
			ItemStack held = snapshot.remove(0);
			if (player != null)
				player.setItemInHand(InteractionHand.MAIN_HAND, held);
			overflowItems = snapshot;
		}
	};

	public enum State {
		WAITING, EXPANDING, RETRACTING, DUMPING;
	}

	public enum Mode {
		PUNCH, USE
	}

	public DeployerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.state = State.WAITING;
		mode = Mode.USE;
		heldItem = ItemStack.EMPTY;
		redstoneLocked = false;
		animatedOffset = LerpedFloat.linear().startWithValue(0);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		filtering = new FilteringBehaviour(this, new DeployerFilterSlot());
		behaviours.add(filtering);
		processingBehaviour = new BeltProcessingBehaviour(this)
				.whenItemEnters((s, i) -> BeltDeployerCallbacks.onItemReceived(s, i, this))
				.whileItemHeld((s, i) -> BeltDeployerCallbacks.whenItemHeld(s, i, this));
		behaviours.add(processingBehaviour);
	}

	@Override
	public void initialize() {
		super.initialize();
		initHandler();
	}

	private void initHandler() {
		if (invHandler != null)
			return;
		if (level instanceof ServerLevel sLevel) {
			player = new DeployerFakePlayer(sLevel, owner);
			if (deferredInventoryList != null) {
				player.getInventory().load(deferredInventoryList);
				deferredInventoryList = null;
				heldItem = player.getMainHandItem();
				sendData();
			}
			Vec3 initialPos = VecHelper.getCenterOf(worldPosition.relative(getBlockState().getValue(FACING)));
			player.setPos(initialPos.x, initialPos.y, initialPos.z);
		}
		invHandler = new DeployerItemHandler(this);
	}

	protected void onExtract(ItemStack stack) {
		if (player != null)
			player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
		sendData();
		setChanged();
	}

	protected int getTimerSpeed() {
		return (int) (getSpeed() == 0 ? 0 : Mth.clamp(Math.abs(getSpeed() * 2), 8, 512));
	}

	@Override
	public void tick() {
		super.tick();

		if (!overflowItems.isEmpty()) {
			overflowItems.removeIf(ItemStack::isEmpty);
		}

		if (getSpeed() == 0)
			return;
		if (!level.isClientSide && player != null && player.blockBreakingProgress != null) {
			if (level.isEmptyBlock(player.blockBreakingProgress.getKey())) {
				level.destroyBlockProgress(player.getId(), player.blockBreakingProgress.getKey(), -1);
				player.blockBreakingProgress = null;
			}
		}
		if (timer > 0) {
			timer -= getTimerSpeed();
			return;
		}
		if (level.isClientSide)
			return;

		if (player == null)
			return;

		ItemStack stack = player.getMainHandItem();
		if (state == State.WAITING) {
			if (!overflowItems.isEmpty()) {
				timer = getTimerSpeed() * 10;
				return;
			}

			boolean changed = false;
			Inventory inventory = player.getInventory();
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				if (overflowItems.size() > 10)
					break;
				ItemStack item = inventory.getItem(i);
				if (item.isEmpty())
					continue;
				if (item != stack || !filtering.test(item)) {
					overflowItems.add(item);
					inventory.setItem(i, ItemStack.EMPTY);
					changed = true;
				}
			}

			if (changed) {
				sendData();
				timer = getTimerSpeed() * 10;
				return;
			}

			Direction facing = getBlockState().getValue(FACING);
			if (mode == Mode.USE
					&& !DeployerHandler.shouldActivate(stack, level, worldPosition.relative(facing, 2), facing)) {
				timer = getTimerSpeed() * 10;
				return;
			}

			if (mode == Mode.PUNCH && !fistBump && startFistBump(facing))
				return;
			if (redstoneLocked)
				return;

			start();
			return;
		}

		if (state == State.EXPANDING) {
			if (fistBump)
				triggerFistBump();
			activate();

			state = State.RETRACTING;
			timer = 1000;
			sendData();
			return;
		}

		if (state == State.RETRACTING) {
			state = State.WAITING;
			timer = 500;
			sendData();
			return;
		}
	}

	protected void start() {
		state = State.EXPANDING;
		Vec3 movementVector = getMovementVector();
		Vec3 rayOrigin = VecHelper.getCenterOf(worldPosition).add(movementVector.scale(3 / 2f));
		Vec3 rayTarget = VecHelper.getCenterOf(worldPosition).add(movementVector.scale(5 / 2f));
		ClipContext rayTraceContext = new ClipContext(rayOrigin, rayTarget, Block.OUTLINE, Fluid.NONE, player);
		BlockHitResult result = level.clip(rayTraceContext);
		reach = (float) (.5f + Math.min(result.getLocation().subtract(rayOrigin).length(), .75f));
		timer = 1000;
		sendData();
	}

	public boolean startFistBump(Direction facing) {
		int i = 0;
		DeployerBlockEntity partner = null;

		for (i = 2; i < 5; i++) {
			BlockPos otherDeployer = worldPosition.relative(facing, i);
			if (!level.isLoaded(otherDeployer))
				return false;
			BlockEntity other = level.getBlockEntity(otherDeployer);
			if (other instanceof DeployerBlockEntity dpe) {
				partner = dpe;
				break;
			}
		}

		if (partner == null)
			return false;

		if (level.getBlockState(partner.getBlockPos()).getValue(FACING).getOpposite() != facing
				|| partner.mode != Mode.PUNCH)
			return false;
		if (partner.getSpeed() == 0)
			return false;

		for (DeployerBlockEntity be : Arrays.asList(this, partner)) {
			be.fistBump = true;
			be.reach = ((i - 2)) * .5f;
			be.timer = 1000;
			be.state = State.EXPANDING;
			be.sendData();
		}

		return true;
	}

	public void triggerFistBump() {
		int i = 0;
		DeployerBlockEntity deployerBlockEntity = null;
		for (i = 2; i < 5; i++) {
			BlockPos pos = worldPosition.relative(getBlockState().getValue(FACING), i);
			if (!level.isLoaded(pos))
				return;
			if (level.getBlockEntity(pos) instanceof DeployerBlockEntity dpe) {
				deployerBlockEntity = dpe;
				break;
			}
		}

		if (deployerBlockEntity == null)
			return;
		if (!deployerBlockEntity.fistBump || deployerBlockEntity.state != State.EXPANDING)
			return;
		if (deployerBlockEntity.timer > 0)
			return;

		fistBump = false;
		deployerBlockEntity.fistBump = false;
		deployerBlockEntity.state = State.RETRACTING;
		deployerBlockEntity.timer = 1000;
		deployerBlockEntity.sendData();

		BlockPos soundLocation = BlockPos.containing(
				Vec3.atCenterOf(worldPosition).add(Vec3.atCenterOf(deployerBlockEntity.getBlockPos())).scale(.5f));
		level.playSound(null, soundLocation, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.BLOCKS, .75f, .75f);
	}

	protected void activate() {
		Vec3 movementVector = getMovementVector();
		Direction direction = getBlockState().getValue(FACING);
		Vec3 center = VecHelper.getCenterOf(worldPosition);
		BlockPos clickedPos = worldPosition.relative(direction, 2);
		player.setXRot(direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0);
		player.setYRot(direction.toYRot());

		if (direction == Direction.DOWN
				&& BlockEntityBehaviour.get(level, clickedPos, TransportedItemStackHandlerBehaviour.TYPE) != null)
			return; // Belt processing handled in BeltDeployerCallbacks

		DeployerHandler.activate(player, center, clickedPos, movementVector, mode);

		if (player != null)
			heldItem = player.getMainHandItem();
	}

	protected Vec3 getMovementVector() {
		if (!AllBlocks.DEPLOYER.has(getBlockState()))
			return Vec3.ZERO;
		return Vec3.atLowerCornerOf(getBlockState().getValue(FACING).getNormal());
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider reg, boolean clientPacket) {
		state = NBTHelper.readEnum(compound, "State", State.class);
		mode = NBTHelper.readEnum(compound, "Mode", Mode.class);
		timer = compound.getInt("Timer");
		redstoneLocked = compound.getBoolean("Powered");
		if (compound.contains("Owner"))
			owner = compound.getUUID("Owner");

		deferredInventoryList = compound.getList("Inventory", Tag.TAG_COMPOUND);
		overflowItems = NBTHelper.readItemList(compound.getList("Overflow", Tag.TAG_COMPOUND), reg);
		if (compound.contains("HeldItem"))
			heldItem = ItemStack.parseOptional(reg, compound.getCompound("HeldItem"));
		super.read(compound, reg, clientPacket);

		if (!clientPacket)
			return;
		fistBump = compound.getBoolean("Fistbump");
		reach = compound.getFloat("Reach");
		if (compound.contains("Particle")) {
			ItemStack particleStack = ItemStack.parseOptional(reg, compound.getCompound("Particle"));
			spawnParticles(VecHelper.getCenterOf(worldPosition).add(getMovementVector().scale(reach + 1)),
					particleStack, this.level);
		}
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider reg, boolean clientPacket) {
		NBTHelper.writeEnum(compound, "Mode", mode);
		NBTHelper.writeEnum(compound, "State", state);
		compound.putInt("Timer", timer);
		compound.putBoolean("Powered", redstoneLocked);
		if (owner != null)
			compound.putUUID("Owner", owner);

		if (player != null) {
			ListTag invNBT = new ListTag();
			player.getInventory().save(invNBT);
			compound.put("Inventory", invNBT);
			compound.put("HeldItem", player.getMainHandItem().saveOptional(reg));
			compound.put("Overflow", NBTHelper.writeItemList(overflowItems, reg));
		} else if (deferredInventoryList != null) {
			compound.put("Inventory", deferredInventoryList);
		}

		super.write(compound, reg, clientPacket);

		if (!clientPacket)
			return;
		compound.putBoolean("Fistbump", fistBump);
		compound.putFloat("Reach", reach);
		if (player == null)
			return;
		compound.put("HeldItem", player.getMainHandItem().saveOptional(reg));
		if (player.spawnedItemEffects != null) {
			compound.put("Particle", player.spawnedItemEffects.saveOptional(reg));
			player.spawnedItemEffects = null;
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider reg) {
		NBTHelper.writeEnum(tag, "Mode", mode);
		super.writeSafe(tag, reg);
	}

	public void redstoneUpdate() {
		if (level.isClientSide)
			return;
		boolean blockPowered = level.hasNeighborSignal(worldPosition);
		if (blockPowered == redstoneLocked)
			return;
		redstoneLocked = blockPowered;
		sendData();
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return super.createRenderBoundingBox().inflate(3);
	}

	public void discardPlayer() {
		if (player == null)
			return;
		player.getInventory().dropAll();
		overflowItems.forEach(itemstack -> player.drop(itemstack, true, false));
		player.discard();
		player = null;
	}

	public void changeMode() {
		mode = mode == Mode.PUNCH ? Mode.USE : Mode.PUNCH;
		setChanged();
		sendData();
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		if (invHandler == null)
			initHandler();
		return invHandler;
	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (getSpeed() == 0)
			return false;
		if (overflowItems.isEmpty())
			return false;
		TooltipHelper.addHint(tooltip, "hint.full_deployer");
		return true;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		CreateLang.translate("tooltip.deployer.header").forGoggles(tooltip);

		CreateLang.translate("tooltip.deployer." + (mode == Mode.USE ? "using" : "punching")).style(ChatFormatting.YELLOW)
				.forGoggles(tooltip);

		if (!heldItem.isEmpty())
			CreateLang.translate("tooltip.deployer.contains",
					Component.translatable(heldItem.getDescriptionId()).getString(), heldItem.getCount())
					.style(ChatFormatting.GREEN).forGoggles(tooltip);

		return true;
	}

	public float getHandOffset(float partialTicks) {
		if (isVirtual())
			return animatedOffset.getValue(partialTicks);

		float progress = 0;
		int timerSpeed = getTimerSpeed();

		if (state == State.EXPANDING) {
			progress = 1 - (timer - partialTicks * timerSpeed) / 1000f;
			if (fistBump)
				progress *= progress;
		}
		if (state == State.RETRACTING)
			progress = (timer - partialTicks * timerSpeed) / 1000f;
		float handLength = 3 / 16f;
		return Math.min(Mth.clamp(progress, 0, 1) * (reach + handLength), 21 / 16f);
	}

	public void setAnimatedOffset(float offset) {
		animatedOffset.setValue(offset);
	}

	@Nullable
	public Recipe<? extends RecipeInput> getRecipe(ItemStack stack) {
		return null;
	}

	public DeployerFakePlayer getPlayer() {
		return player;
	}

	public List<ItemStack> getOverflowItems() {
		return overflowItems;
	}

	public static void spawnParticles(Vec3 location, ItemStack polishedStack, Level world) {
		for (int i = 0; i < 20; i++) {
			Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, 1 / 8f);
			world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, polishedStack), location.x, location.y,
				location.z, motion.x, motion.y, motion.z);
		}
	}
}

package com.simibubi.create.content.kinetics.crusher;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.math.VecHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CrushingWheelControllerBlockEntity extends SmartBlockEntity implements SidedStorageBlockEntity, Clearable {

	public Entity processingEntity;
	private UUID entityUUID;
	protected boolean searchForEntity;

	public ProcessingInventory inventory;
	public float crushingspeed;

	public CrushingWheelControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new ProcessingInventory(this::itemInserted) {

			@Override
			public boolean isItemValid(int slot, ItemVariant stack, int amount) {
				return super.isItemValid(slot, stack, amount) && processingEntity == null;
			}

		};
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		super.tick();
		if (searchForEntity) {
			searchForEntity = false;
			List<Entity> search = level.getEntities((Entity) null, new AABB(getBlockPos()),
					e -> entityUUID.equals(e.getUUID()));
			if (search.isEmpty())
				clear();
			else
				processingEntity = search.get(0);
		}

		if (!isOccupied())
			return;
		if (crushingspeed == 0)
			return;

		float speed = crushingspeed * 4;

		Vec3 centerPos = VecHelper.getCenterOf(worldPosition);
		Direction facing = getBlockState().getValue(CrushingWheelControllerBlock.FACING);
		int offset = facing.getAxisDirection().getStep();
		Vec3 outSpeed = new Vec3((facing.getAxis() == Axis.X ? 0.25D : 0.0D) * offset,
				offset == 1 ? (facing.getAxis() == Axis.Y ? 0.5D : 0.0D) : 0.0D,
				(facing.getAxis() == Axis.Z ? 0.25D : 0.0D) * offset);
		Vec3 outPos = centerPos.add((facing.getAxis() == Axis.X ? .55f * offset : 0f),
				(facing.getAxis() == Axis.Y ? .55f * offset : 0f), (facing.getAxis() == Axis.Z ? .55f * offset : 0f));

		if (!hasEntity()) {

			float processingSpeed = Mth.clamp(
					(speed) / (!inventory.appliedRecipe ? Mth.log2(inventory.getStackInSlot(0).getCount()) : 1), .25f,
					20);
			inventory.remainingTime -= processingSpeed;
			spawnParticles(inventory.getStackInSlot(0));

			if (level.isClientSide)
				return;

			if (inventory.remainingTime < 20 && !inventory.appliedRecipe) {
				inventory.appliedRecipe = true;
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
				return;
			}

			if (inventory.remainingTime > 0) {
				return;
			}
			inventory.remainingTime = 0;

			// Eject Items
			for (int slot = 0; slot < inventory.getSlotCount(); slot++) {
				ItemStack stack = inventory.getStackInSlot(slot);
				if (stack.isEmpty())
					continue;
				ItemEntity entityIn = new ItemEntity(level, outPos.x, outPos.y, outPos.z, stack);
				entityIn.setDeltaMovement(outSpeed);
				entityIn.getCustomData().put("BypassCrushingWheel", NbtUtils.writeBlockPos(worldPosition));
				level.addFreshEntity(entityIn);
			}
			inventory.clear();
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);

			return;
		}

		if (!processingEntity.isAlive()
				|| !processingEntity.getBoundingBox().intersects(new AABB(worldPosition).inflate(.5f))) {
			clear();
			return;
		}

		double xMotion = ((worldPosition.getX() + .5f) - processingEntity.getX()) / 2f;
		double zMotion = ((worldPosition.getZ() + .5f) - processingEntity.getZ()) / 2f;
		if (processingEntity.isShiftKeyDown())
			xMotion = zMotion = 0;
		double movement = Math.max(-speed / 4f, -.5f) * -offset;
		processingEntity.setDeltaMovement(
				new Vec3(facing.getAxis() == Axis.X ? movement : xMotion, facing.getAxis() == Axis.Y ? movement : 0f,
						facing.getAxis() == Axis.Z ? movement : zMotion));

		if (level.isClientSide)
			return;

		if (!(processingEntity instanceof ItemEntity)) {
			Vec3 entityOutPos = outPos.add(facing.getAxis() == Axis.X ? .5f * offset : 0f,
					facing.getAxis() == Axis.Y ? .5f * offset : 0f, facing.getAxis() == Axis.Z ? .5f * offset : 0f);
			int crusherDamage = AllConfigs.server().kinetics.crushingDamage.get();

			if (processingEntity instanceof LivingEntity living) {
				if (((living.getHealth() - crusherDamage <= 0))
						&& (living.hurtTime <= 0)) {
					processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
				}
			}
			processingEntity.hurt(CreateDamageSources.crush(level), crusherDamage);
			if (!processingEntity.isAlive()) {
				processingEntity.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z);
			}
			return;
		}

		ItemEntity itemEntity = (ItemEntity) processingEntity;
		itemEntity.setPickUpDelay(20);
		if (facing.getAxis() == Axis.Y) {
			if (processingEntity.getY() * -offset < (centerPos.y - .25f) * -offset) {
				intakeItem(itemEntity);
			}
		} else if (facing.getAxis() == Axis.Z) {
			if (processingEntity.getZ() * -offset < (centerPos.z - .25f) * -offset) {
				intakeItem(itemEntity);
			}
		} else {
			if (processingEntity.getX() * -offset < (centerPos.x - .25f) * -offset) {
				intakeItem(itemEntity);
			}
		}
	}

	private void intakeItem(ItemEntity itemEntity) {
		inventory.clear();
		inventory.setStackInSlot(0, itemEntity.getItem().copy());
		itemInserted(inventory.getStackInSlot(0));
		itemEntity.discard();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 16);
	}

	protected void spawnParticles(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return;

		ParticleOptions particleData = null;
		if (stack.getItem() instanceof BlockItem)
			particleData = new BlockParticleOption(ParticleTypes.BLOCK,
					((BlockItem) stack.getItem()).getBlock().defaultBlockState());
		else
			particleData = new ItemParticleOption(ParticleTypes.ITEM, stack);

		RandomSource r = level.random;
		for (int i = 0; i < 4; i++)
			level.addParticle(particleData, worldPosition.getX() + r.nextFloat(), worldPosition.getY() + r.nextFloat(),
					worldPosition.getZ() + r.nextFloat(), 0, 0, 0);
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		if (hasEntity())
			compound.put("Entity", NbtUtils.createUUID(entityUUID));
		compound.put("Inventory", inventory.serializeNBT(registries));
		compound.putFloat("Speed", crushingspeed);
		super.write(compound, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (compound.contains("Entity") && !isOccupied()) {
			entityUUID = NbtUtils.loadUUID(compound.get("Entity"));
			this.searchForEntity = true;
		}
		crushingspeed = compound.getFloat("Speed");
		inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	public void startCrushing(Entity entity) {
		processingEntity = entity;
		entityUUID = entity.getUUID();
	}

	private void itemInserted(ItemStack stack) {
		inventory.remainingTime = 100;
		inventory.appliedRecipe = false;
	}

	@Nullable
	@Override
	public Storage<ItemVariant> getItemStorage(@Nullable Direction face) {
		return inventory;
	}

	public void clear() {
		processingEntity = null;
		entityUUID = null;
	}

	public boolean isOccupied() {
		return hasEntity() || !inventory.isEmpty();
	}

	public boolean hasEntity() {
		return processingEntity != null;
	}

}

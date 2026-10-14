package com.simibubi.create.content.contraptions.chassis;

import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class StickerBlockEntity extends SmartBlockEntity {

	LerpedFloat piston;
	boolean update;

	public StickerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		piston = LerpedFloat.linear();
		update = false;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!level.isClientSide)
			return;
		piston.startWithValue(isBlockStateExtended() ? 1 : 0);
	}

	public boolean isBlockStateExtended() {
		BlockState blockState = getBlockState();
		boolean extended = AllBlocks.STICKER.has(blockState) && blockState.getValue(StickerBlock.EXTENDED);
		return extended;
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide)
			return;
		piston.tickChaser();

		if (isAttachedToBlock() && piston.getValue(0) != piston.getValue() && piston.getValue() == 1) {
			spawnParticles(level, worldPosition, getBlockState().getValue(StickerBlock.FACING));
			CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> playSound(true));
		}

		if (!update)
			return;
		update = false;
		int target = isBlockStateExtended() ? 1 : 0;
		if (isAttachedToBlock() && target == 0 && piston.getChaseTarget() == 1)
			CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> playSound(false));
		piston.chase(target, .4f, Chaser.LINEAR);

		CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate(this));
	}

	public boolean isAttachedToBlock() {
		BlockState blockState = getBlockState();
		if (!AllBlocks.STICKER.has(blockState))
			return false;
		Direction direction = blockState.getValue(StickerBlock.FACING);
		BlockPos targetPos = worldPosition.relative(direction);
		BlockState targetState = level.getBlockState(targetPos);
		Direction targetFace = direction.getOpposite();
		if (BlockMovementChecks.isBlockAttachedTowards(targetState, level, targetPos, targetFace))
			return true;
		if (!BlockMovementChecks.isMovementNecessary(targetState, level, targetPos))
			return false;
		if (BlockMovementChecks.isNotSupportive(targetState, targetFace))
			return false;
		return true;
	}

	private static void spawnParticles(Level world, BlockPos pos, Direction direction) {
		Vec3 vec = Vec3.atLowerCornerOf(direction.getNormal());
		Vec3 plane = VecHelper.axisAlingedPlaneOf(vec);
		Vec3 facePos = VecHelper.getCenterOf(pos).add(vec.scale(.5f));

		plane = plane.scale(1f);
		ItemStack stack = new ItemStack(Items.SLIME_BALL);

		for (int i = 40; i > 0; i--) {
			Vec3 offset = VecHelper.rotate(plane, 360 * world.random.nextFloat(), direction.getAxis());
			Vec3 motion = offset.normalize().scale(1 / 16f);
			offset = new Vec3(Mth.clamp(offset.x, -.5, .5), Mth.clamp(offset.y, -.5, .5), Mth.clamp(offset.z, -.5, .5));
			Vec3 particlePos = facePos.add(offset);
			world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), particlePos.x, particlePos.y,
				particlePos.z, motion.x, motion.y, motion.z);
		}
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (clientPacket)
			update = true;
	}

	@Environment(EnvType.CLIENT)
	public void playSound(boolean attach) {
		AllSoundEvents.SLIME_ADDED.play(level, Minecraft.getInstance().player, worldPosition, 0.35f, attach ? 0.75f : 0.2f);
	}

}

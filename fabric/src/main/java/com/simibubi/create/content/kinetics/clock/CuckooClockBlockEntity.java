package com.simibubi.create.content.kinetics.clock;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CuckooClockBlockEntity extends KineticBlockEntity {

	public LerpedFloat hourHand = LerpedFloat.angular();
	public LerpedFloat minuteHand = LerpedFloat.angular();
	public LerpedFloat animationProgress = LerpedFloat.linear();
	public Animation animationType;
	private boolean sendAnimationUpdate;

	enum Animation {
		PIG, CREEPER, SURPRISE, NONE;
	}

	public CuckooClockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		animationType = Animation.NONE;
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (clientPacket && compound.contains("Animation")) {
			animationType = NBTHelper.readEnum(compound, "Animation", Animation.class);
			animationProgress.startWithValue(0);
		}
	}

	@Override
	public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		if (clientPacket && sendAnimationUpdate) {
			sendAnimationUpdate = false;
			NBTHelper.writeEnum(compound, "Animation", animationType);
		}
		super.write(compound, registries, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide) {
			boolean isSurprise = animationType == Animation.SURPRISE;
			float speed = isSurprise ? 3 : 1;

			if (animationType != Animation.NONE) {
				float target = animationProgress.getValue() + speed;
				animationProgress.setValue(target);
				int value = (int) animationProgress.getValue();

				if (value > 100) {
					animationProgress.startWithValue(0);
					animationType = Animation.NONE;
				}

				if (value == 1 && isSurprise) {
					Vec3 pos = VecHelper.offsetRandomly(VecHelper.getCenterOf(this.worldPosition), level.random, .5f);
					level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 0, 0, 0);
				}
				if (value == 40 && isSurprise)
					playSound(SoundEvents.TNT_PRIMED, 1f, 1f);

				int step = isSurprise ? 3 : 15;
				for (int phase = 30; phase <= 60; phase += step) {
					if (value == phase - step / 3)
						playSound(SoundEvents.CHEST_OPEN, 1 / 16f, 2f);
					if (value == phase) {
						if (animationType == Animation.PIG)
							playSound(SoundEvents.PIG_AMBIENT, 1 / 4f, 1f);
						else
							playSound(SoundEvents.CREEPER_HURT, 1 / 4f, 3f);
					}
					if (value == phase + step / 3)
						playSound(SoundEvents.CHEST_CLOSE, 1 / 16f, 2f);
				}
			}

			return;
		}

		if (getSpeed() == 0)
			return;

		int dayTime = (int) (level.getDayTime() % 24000);
		int hours = (dayTime / 1000 + 6) % 24;
		int minutes = (dayTime % 1000) * 60 / 1000;

		moveHands(hours, minutes);

		if (hours == 12 && minutes == 0)
			startAnimation(Animation.PIG);
		else if (hours == 18 && minutes == 0)
			startAnimation(Animation.CREEPER);
	}

	public void startAnimation(Animation animation) {
		animationType = animation;
		if (animation != null && CuckooClockBlock.containsSurprise(getBlockState()))
			animationType = Animation.SURPRISE;
		animationProgress.startWithValue(0);
		sendAnimationUpdate = true;
		sendData();
	}

	public void moveHands(int hours, int minutes) {
		float hourTarget = (float) (360 / 12 * (hours % 12));
		float minuteTarget = (float) (360 / 60 * minutes);

		hourHand.chase(hourTarget, .2f, Chaser.EXP);
		minuteHand.chase(minuteTarget, .2f, Chaser.EXP);

		hourHand.tickChaser();
		minuteHand.tickChaser();
	}

	private void playSound(SoundEvent sound, float volume, float pitch) {
		Vec3 vec = VecHelper.getCenterOf(worldPosition);
		level.playLocalSound(vec.x, vec.y, vec.z, sound, SoundSource.BLOCKS, volume, pitch, false);
	}
}

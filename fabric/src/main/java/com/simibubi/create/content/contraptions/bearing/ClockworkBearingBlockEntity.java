package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;

import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkBearingBlockEntity extends KineticBlockEntity
	implements IBearingBlockEntity, IDisplayAssemblyExceptions {

	public float hourAngle;
	public float minuteAngle;
	public float clientHourAngleDiff;
	public float clientMinuteAngleDiff;

	public boolean running;
	public boolean assembleNextTick;
	public AssemblyException lastException;

	public ClockworkBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean isWoodenTop() {
		return false;
	}

	@Override
	public float getInterpolatedAngle(float partialTicks) {
		return hourAngle;
	}

	@Override
	public void setAngle(float forcedAngle) {
		hourAngle = forcedAngle;
	}

	public void disassemble() {
		if (!running)
			return;
		running = false;
		sendData();
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		hourAngle = compound.getFloat("HourAngle");
		minuteAngle = compound.getFloat("MinuteAngle");
		running = compound.getBoolean("Running");
		lastException = AssemblyException.read(compound, registries);
		super.read(compound, registries, clientPacket);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		compound.putFloat("HourAngle", hourAngle);
		compound.putFloat("MinuteAngle", minuteAngle);
		compound.putBoolean("Running", running);
		AssemblyException.write(compound, registries, lastException);
		super.write(compound, registries, clientPacket);
	}

	public enum ClockHands implements INamedIconOptions {
		HOUR_FIRST(AllIcons.I_HOUR_HAND_FIRST),
		MINUTE_FIRST(AllIcons.I_MINUTE_HAND_FIRST),
		HOUR_FIRST_24(AllIcons.I_HOUR_HAND_FIRST_24);

		private final String translationKey;
		private final AllIcons icon;

		ClockHands(AllIcons icon) {
			this.icon = icon;
			this.translationKey = "create.contraptions.clockwork." + Lang.asId(name());
		}

		@Override
		public AllIcons getIcon() {
			return icon;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}

package com.simibubi.create.foundation.blockEntity.behaviour.fluid;

import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class SmartFluidTankBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<SmartFluidTankBehaviour>
	TYPE = new BehaviourType<>(), INPUT = new BehaviourType<>("Input"), OUTPUT = new BehaviourType<>("Output");

	private static final int SYNC_RATE = 8;

	protected int syncCooldown;
	protected boolean queuedSync;
	protected TankSegment[] tanks;
	protected Object capability;
	protected boolean extractionAllowed;
	protected boolean insertionAllowed;
	protected Runnable fluidUpdateCallback;

	private BehaviourType<SmartFluidTankBehaviour> behaviourType;

	public static SmartFluidTankBehaviour single(SmartBlockEntity be, int capacity) {
		return new SmartFluidTankBehaviour(TYPE, be, 1, capacity, false);
	}

	public SmartFluidTankBehaviour(BehaviourType<SmartFluidTankBehaviour> type, SmartBlockEntity be, int tanks,
		int tankCapacity, boolean enforceVariety) {
		super(be);
		insertionAllowed = true;
		extractionAllowed = true;
		behaviourType = type;
		this.tanks = new TankSegment[tanks];
		for (int i = 0; i < tanks; i++) {
			this.tanks[i] = SmartFluidTankHooks.createTankSegment(this, tankCapacity);
		}
		capability = SmartFluidTankHooks.createHandler(this, this.tanks, enforceVariety);
		fluidUpdateCallback = () -> {
		};
	}

	public SmartFluidTankBehaviour whenFluidUpdates(Runnable fluidUpdateCallback) {
		this.fluidUpdateCallback = fluidUpdateCallback;
		return this;
	}

	public SmartFluidTankBehaviour allowInsertion() {
		insertionAllowed = true;
		return this;
	}

	public SmartFluidTankBehaviour allowExtraction() {
		extractionAllowed = true;
		return this;
	}

	public SmartFluidTankBehaviour forbidInsertion() {
		insertionAllowed = false;
		return this;
	}

	public SmartFluidTankBehaviour forbidExtraction() {
		extractionAllowed = false;
		return this;
	}

	public boolean isInsertionAllowed() {
		return insertionAllowed;
	}

	public boolean isExtractionAllowed() {
		return extractionAllowed;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getWorld().isClientSide)
			return;
		forEach(ts -> {
			ts.fluidLevel.forceNextSync();
			ts.onFluidStackChanged();
		});
	}

	@Override
	public void tick() {
		super.tick();

		if (syncCooldown > 0) {
			syncCooldown--;
			if (syncCooldown == 0 && queuedSync)
				updateFluids();
		}

		forEach(be -> {
			LerpedFloat fluidLevel = be.getFluidLevel();
			if (fluidLevel != null)
				fluidLevel.tickChaser();
		});
	}

	public void sendDataImmediately() {
		syncCooldown = 0;
		queuedSync = false;
		updateFluids();
	}

	public void sendDataLazily() {
		if (syncCooldown > 0) {
			queuedSync = true;
			return;
		}
		updateFluids();
		queuedSync = false;
		syncCooldown = SYNC_RATE;
	}

	protected void updateFluids() {
		fluidUpdateCallback.run();
		blockEntity.sendData();
		blockEntity.setChanged();
	}

	@Override
	public void unload() {
		super.unload();
		SmartFluidTankHooks.invalidateCapabilities(blockEntity);
	}

	@SuppressWarnings("unchecked")
	public <T> T getPrimaryHandler() {
		return (T) getPrimaryTank().getTank();
	}

	public TankSegment getPrimaryTank() {
		return tanks[0];
	}

	public TankSegment[] getTanks() {
		return tanks;
	}

	public boolean isEmpty() {
		for (TankSegment tankSegment : tanks)
			if (!tankSegment.isEmpty())
				return false;
		return true;
	}

	public void forEach(Consumer<TankSegment> action) {
		for (TankSegment tankSegment : tanks)
			action.accept(tankSegment);
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability() {
		return (T) capability;
	}

	@Override
	public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(nbt, registries, clientPacket);
		ListTag tanksNBT = new ListTag();
		forEach(ts -> tanksNBT.add(ts.writeNBT(registries)));
		nbt.put(getType().getName() + "Tanks", tanksNBT);
	}

	@Override
	public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(nbt, registries, clientPacket);
		MutableInt index = new MutableInt(0);
		NBTHelper.iterateCompoundList(nbt.getList(getType().getName() + "Tanks", Tag.TAG_COMPOUND), c -> {
			if (index.intValue() >= tanks.length)
				return;
			tanks[index.intValue()].readNBT(c, registries, clientPacket);
			index.increment();
		});
	}

	public abstract static class TankSegment {
		protected final SmartFluidTankBehaviour behaviour;
		protected final LerpedFloat fluidLevel;

		public TankSegment(SmartFluidTankBehaviour behaviour) {
			this.behaviour = behaviour;
			this.fluidLevel = LerpedFloat.linear()
				.startWithValue(0)
				.chase(0, .25, Chaser.EXP);
		}

		public LerpedFloat getFluidLevel() {
			return fluidLevel;
		}

		public abstract <F> F getRenderedFluid();

		public abstract <T> T getTank();

		public abstract float getTotalUnits(float partialTicks);

		public abstract boolean isEmpty(float partialTicks);

		public abstract boolean isEmpty();

		public CompoundTag writeNBT(HolderLookup.Provider registries) {
			CompoundTag compound = new CompoundTag();
			writeTankNBT(compound, registries);
			compound.put("Level", fluidLevel.writeNBT());
			return compound;
		}

		public void readNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
			readTankNBT(compound, registries, clientPacket);
			fluidLevel.readNBT(compound.getCompound("Level"), clientPacket);
		}

		public abstract void writeTankNBT(CompoundTag compound, HolderLookup.Provider registries);

		public abstract void readTankNBT(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket);

		public abstract void onFluidStackChanged();
	}

	public interface InternalFluidHandler {
		int forceFill(Object resource, Object action);
	}

	@Override
	public BehaviourType<?> getType() {
		return behaviourType;
	}
}

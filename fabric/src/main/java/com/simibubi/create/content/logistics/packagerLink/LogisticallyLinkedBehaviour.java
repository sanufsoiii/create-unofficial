package com.simibubi.create.content.logistics.packagerLink;

import java.util.UUID;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class LogisticallyLinkedBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<LogisticallyLinkedBehaviour> TYPE = new BehaviourType<>();

	public int redstonePower;
	public UUID freqId;
	private boolean global;

	public enum RequestType {
		RESTOCK, REDSTONE, PLAYER
	}

	public LogisticallyLinkedBehaviour(SmartBlockEntity be, boolean global) {
		super(be);
		this.global = global;
		this.freqId = UUID.randomUUID();
	}

	public void redstonePowerChanged(int power) {
		if (power == redstonePower)
			return;
		redstonePower = power;
		blockEntity.setChanged();
	}

	public boolean mayInteract(Player player) {
		return true;
	}

	public boolean mayInteractMessage(Player player) {
		return true;
	}

	public boolean mayAdministrate(Player player) {
		return true;
	}

	@Override
	public boolean isSafeNBT() {
		return true;
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putUUID("Freq", freqId);
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putUUID("Freq", freqId);
		tag.putInt("Power", redstonePower);
	}

	@Override
	public void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if (tag.hasUUID("Freq"))
			freqId = tag.getUUID("Freq");
		redstonePower = tag.getInt("Power");
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

}

package com.simibubi.create.content.kinetics.press;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class PressingBehaviour extends BlockEntityBehaviour {

	public static final BehaviourType<PressingBehaviour> TYPE = new BehaviourType<>();
	public static final int CYCLE = 240;
	public static final int ENTITY_SCAN = 10;

	public List<ItemStack> particleItems = new ArrayList<>();

	public int prevRunningTicks;
	public int runningTicks;
	public boolean running;
	public boolean finished;
	public Mode mode = Mode.WORLD;

	public PressingBehaviour(SmartBlockEntity be) {
		super(be);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	public float getRenderedHeadOffset(float partialTicks) {
		if (!running)
			return 0;
		int runningTicks = Math.abs(this.runningTicks);
		float ticks = Mth.lerp(partialTicks, prevRunningTicks, runningTicks);
		if (runningTicks < (CYCLE * 2) / 3)
			return (float) Mth.clamp(Math.pow(ticks / CYCLE * 2, 3), 0, 1);
		return Mth.clamp((CYCLE - ticks) / CYCLE * 3, 0, 1);
	}

	public void start(Mode mode) {
		this.mode = mode;
		running = true;
		prevRunningTicks = 0;
		runningTicks = 0;
		particleItems.clear();
		blockEntity.sendData();
	}

	public enum Mode {
		WORLD(1), BELT(19f / 16f), BASIN(22f / 16f);

		public float headOffset;

		Mode(float headOffset) {
			this.headOffset = headOffset;
		}
	}
}

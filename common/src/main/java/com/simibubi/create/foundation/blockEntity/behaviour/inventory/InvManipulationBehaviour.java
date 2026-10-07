package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Predicates;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.CommonItemHelper.ExtractionCountMode;
import com.simibubi.create.foundation.item.ItemTransferUtil;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InvManipulationBehaviour extends CapManipulationBehaviourBase<Object, InvManipulationBehaviour> {

	// Extra types available for multibehaviour
	public static final BehaviourType<InvManipulationBehaviour>

	TYPE = new BehaviourType<>(), EXTRACT = new BehaviourType<>(), INSERT = new BehaviourType<>();

	private BehaviourType<InvManipulationBehaviour> behaviourType;

	public static InvManipulationBehaviour forExtraction(SmartBlockEntity be, InterfaceProvider target) {
		return new InvManipulationBehaviour(EXTRACT, be, target);
	}

	public static InvManipulationBehaviour forInsertion(SmartBlockEntity be, InterfaceProvider target) {
		return new InvManipulationBehaviour(INSERT, be, target);
	}

	public InvManipulationBehaviour(SmartBlockEntity be, InterfaceProvider target) {
		this(TYPE, be, target);
	}

	private InvManipulationBehaviour(BehaviourType<InvManipulationBehaviour> type, SmartBlockEntity be,
		InterfaceProvider target) {
		super(be, target);
		behaviourType = type;
	}

	@Nullable
	public <T> T getIdentifiedInventory() {
		return InvManipulationHooks.getIdentifiedInventory(this);
	}

	@Override
	protected Object findCapability(Level world, net.minecraft.core.BlockPos pos, Direction side) {
		return ItemTransferUtil.getInventory(world, pos, side);
	}


	public ItemStack extract() {
		return extract(getModeFromFilter(), getAmountFromFilter());
	}

	public ItemStack extract(ExtractionCountMode mode, int amount) {
		return extract(mode, amount, Predicates.alwaysTrue());
	}

	public ItemStack extract(ExtractionCountMode mode, int amount, Predicate<ItemStack> filter) {
		boolean shouldSimulate = simulateNext;
		simulateNext = false;

		if (getWorld().isClientSide)
			return ItemStack.EMPTY;
		Object inventory = targetCapability;
		if (inventory == null)
			return ItemStack.EMPTY;

		Predicate<ItemStack> test = getFilterTest(filter);
		return ItemTransferUtil.extract(inventory, test, mode, amount, shouldSimulate);
	}

	public ItemStack insert(ItemStack stack) {
		boolean shouldSimulate = simulateNext;
		simulateNext = false;
		Object inventory = targetCapability;
		if (inventory == null)
			return stack;
		return ItemTransferUtil.insertItem(inventory, stack, shouldSimulate);
	}

	protected Predicate<ItemStack> getFilterTest(Predicate<ItemStack> customFilter) {
		Predicate<ItemStack> test = customFilter;
		FilteringBehaviour filter = blockEntity.getBehaviour(FilteringBehaviour.TYPE);
		if (filter != null)
			test = customFilter.and(filter::test);
		return test;
	}

	@Override
	public BehaviourType<?> getType() {
		return behaviourType;
	}

}

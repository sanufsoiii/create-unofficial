package com.simibubi.create.foundation.blockEntity.behaviour.inventory;

import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class TankManipulationBehaviour extends CapManipulationBehaviourBase<Storage<FluidVariant>, TankManipulationBehaviour> {

	public static final BehaviourType<TankManipulationBehaviour> OBSERVE = new BehaviourType<>();
	private BehaviourType<TankManipulationBehaviour> behaviourType;

	public TankManipulationBehaviour(SmartBlockEntity be, InterfaceProvider target) {
		this(OBSERVE, be, target);
	}

	private TankManipulationBehaviour(BehaviourType<TankManipulationBehaviour> type, SmartBlockEntity be,
		InterfaceProvider target) {
		super(be, target);
		behaviourType = type;
	}

	public FluidStack extractAny() {
		if (!hasInventory())
			return FluidStack.empty();
		Storage<FluidVariant> inventory = getInventory();
		if (inventory == null || !inventory.supportsExtraction())
			return FluidStack.empty();
		Predicate<FluidVariant> filterTest = getFilterTest(Predicates.alwaysTrue());
		try (Transaction tx = Transaction.openOuter()) {
			for (StorageView<FluidVariant> view : inventory) {
				if (view.isResourceBlank() || view.getAmount() <= 0)
					continue;
				FluidVariant variant = view.getResource();
				if (!filterTest.test(variant))
					continue;
				long extracted = view.extract(variant, Math.min(view.getAmount(), FluidConstants.BUCKET), tx);
				if (extracted > 0) {
					if (!simulateNext) {
						tx.commit();
					}
					simulateNext = false;
					return FluidStackHooksFabric.fromFabric(variant, extracted);
				}
			}
		}
		simulateNext = false;
		return FluidStack.empty();
	}

	protected Predicate<FluidVariant> getFilterTest(Predicate<FluidVariant> customFilter) {
		Predicate<FluidVariant> test = customFilter;
		FilteringBehaviour filter = blockEntity.getBehaviour(FilteringBehaviour.TYPE);
		if (filter != null)
			test = customFilter.and(fv -> filter.test(FluidStackHooksFabric.fromFabric(fv, FluidConstants.BUCKET)));
		return test;
	}

	@Override
	protected Storage<FluidVariant> findCapability(Level world, BlockPos pos, Direction side) {
		return FluidStorage.SIDED.find(world, pos, side);
	}

	@Override
	public BehaviourType<?> getType() {
		return behaviourType;
	}

}

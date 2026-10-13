package com.simibubi.create.content.kinetics.deployer;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity.Mode;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity.State;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BeltDeployerCallbacks {

	public static ProcessingResult onItemReceived(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
		DeployerBlockEntity blockEntity) {

		if (blockEntity.getSpeed() == 0)
			return ProcessingResult.PASS;
		if (blockEntity.mode == Mode.PUNCH)
			return ProcessingResult.PASS;
		BlockState blockState = blockEntity.getBlockState();
		if (!blockState.hasProperty(FACING) || blockState.getValue(FACING) != Direction.DOWN)
			return ProcessingResult.PASS;
		if (blockEntity.state != State.WAITING)
			return ProcessingResult.HOLD;
		if (blockEntity.redstoneLocked)
			return ProcessingResult.PASS;

		DeployerFakePlayer player = blockEntity.getPlayer();
		ItemStack held = player == null ? ItemStack.EMPTY : player.getMainHandItem();

		if (held.isEmpty())
			return ProcessingResult.HOLD;

		return ProcessingResult.PASS;
	}

	public static ProcessingResult whenItemHeld(TransportedItemStack s, TransportedItemStackHandlerBehaviour i,
		DeployerBlockEntity blockEntity) {

		if (blockEntity.getSpeed() == 0)
			return ProcessingResult.PASS;
		BlockState blockState = blockEntity.getBlockState();
		if (!blockState.hasProperty(FACING) || blockState.getValue(FACING) != Direction.DOWN)
			return ProcessingResult.PASS;

		DeployerFakePlayer player = blockEntity.getPlayer();
		ItemStack held = player == null ? ItemStack.EMPTY : player.getMainHandItem();
		if (held.isEmpty())
			return ProcessingResult.HOLD;

		return ProcessingResult.PASS;
	}

}

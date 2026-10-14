package com.simibubi.create.content.fluids;

import com.simibubi.create.api.event.PipeCollisionEvent;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.BlockHelper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FluidReactions {

	public static void handlePipeFlowCollision(Level level, BlockPos pos, FluidStack fluid, FluidStack fluid2) {
		Fluid f1 = fluid.getFluid();
		Fluid f2 = fluid2.getFluid();

		// AdvancementBehaviour.tryAward(level, pos, AllAdvancements.CROSS_STREAMS);
		BlockHelper.destroyBlock(level, pos, 1);

		PipeCollisionEvent.Flow event = new PipeCollisionEvent.Flow(level, pos, f1, f2, null);
		PipeCollisionEvent.FLOW.invoker().handleFlow(event);
		if (event.getState() != null)
			level.setBlockAndUpdate(pos, event.getState());
	}

	public static void handlePipeFlowCollisionFallback(PipeCollisionEvent.Flow event) {
		Fluid f1 = event.getFirstFluid();
		Fluid f2 = event.getSecondFluid();

		if (f1 == Fluids.WATER && f2 == Fluids.LAVA || f2 == Fluids.WATER && f1 == Fluids.LAVA) {
			event.setState(Blocks.COBBLESTONE.defaultBlockState());
		}
		// TODO: Restore AllFluids custom lava interactions when AllFluids is ported
	}

	public static void handlePipeSpillCollision(Level level, BlockPos pos, Fluid pipeFluid, FluidState worldFluid) {
		Fluid pf = FluidHelper.convertToStill(pipeFluid);
		Fluid wf = worldFluid.getType();

		PipeCollisionEvent.Spill event = new PipeCollisionEvent.Spill(level, pos, wf, pf, null);
		PipeCollisionEvent.SPILL.invoker().handleSpill(event);
		if (event.getState() != null) {
			level.setBlockAndUpdate(pos, event.getState());
		}
	}

	public static void handlePipeSpillCollisionFallback(PipeCollisionEvent.Spill event) {
		Fluid pf = event.getPipeFluid();
		Fluid wf = event.getWorldFluid();

		if (FluidHelper.isTag(pf, FluidTags.WATER) && wf == Fluids.LAVA) {
			event.setState(Blocks.OBSIDIAN.defaultBlockState());
		} else if (pf == Fluids.WATER && wf == Fluids.FLOWING_LAVA) {
			event.setState(Blocks.COBBLESTONE.defaultBlockState());
		} else if (pf == Fluids.LAVA && wf == Fluids.WATER) {
			event.setState(Blocks.STONE.defaultBlockState());
		} else if (pf == Fluids.LAVA && wf == Fluids.FLOWING_WATER) {
			event.setState(Blocks.COBBLESTONE.defaultBlockState());
		}
		// TODO: Restore AllFluids custom lava interactions when AllFluids is ported
	}

}

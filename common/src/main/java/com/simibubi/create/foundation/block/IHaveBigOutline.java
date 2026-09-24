package com.simibubi.create.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Implementing this interface will allow you to have bigger outlines when overriding {@link BlockBehaviour#getInteractionShape(BlockState, BlockGetter, BlockPos)}
 */
public interface IHaveBigOutline { }

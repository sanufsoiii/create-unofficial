package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.contraption.render.IContraptionRenderer;
import com.simibubi.create.content.contraptions.Contraption;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;

/**
 * Flywheel-backed implementation of IContraptionRenderer for NeoForge/Fabric.
 */
public class FlywheelContraptionRenderer implements IContraptionRenderer {

    @Override
    public boolean isAvailable(Level level) {
        return VisualizationManager.supportsVisualization(level);
    }

    @Override
    public void renderContraption(Object contraptionObj, PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {
        // Handled by Flywheel entity visualization pipeline in ContraptionVisual
    }

    @Override
    public void invalidate(Object contraptionObj) {
        // Cache invalidation handled by SuperByteBufferCache on demand
    }
}

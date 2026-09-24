package com.simibubi.create.api.contraption.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Pluggable rendering engine interface for contraptions.
 * Decouples contraption lifecycle and rendering from Flywheel, allowing custom
 * rendering backends (e.g. Aeronautics custom engine, vanilla buffers, etc.).
 */
public interface IContraptionRenderer {

    /**
     * Whether this renderer can handle visual/instanced rendering for the given world.
     */
    boolean isAvailable(Level level);

    /**
     * Renders a contraption using the active rendering backend.
     *
     * @param contraption the contraption instance being rendered
     * @param poseStack   the active model pose stack
     * @param buffers     buffer source for tessellation
     * @param light       packed light level
     * @param partialTick animation partial tick
     */
    void renderContraption(Object contraption, PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick);

    /**
     * Invalidates cached GPU meshes, instances, or buffers associated with the contraption.
     * Called when the contraption stops moving, is disassembled, or modified.
     *
     * @param contraption the contraption instance to invalidate
     */
    void invalidate(Object contraption);

    /**
     * Notify the renderer that a block in the contraption was updated or changed state.
     */
    default void onBlockChanged(Object contraption, Object localPos) {}
}

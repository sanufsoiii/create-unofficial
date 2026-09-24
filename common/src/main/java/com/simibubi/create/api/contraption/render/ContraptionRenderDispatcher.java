package com.simibubi.create.api.contraption.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Dispatcher and registry for contraption render engines.
 * Addons (such as Aeronautics or custom shader engines) can register their own
 * {@link IContraptionRenderer} via {@link #registerRenderer(IContraptionRenderer)}.
 */
public final class ContraptionRenderDispatcher {

    private static final IContraptionRenderer NOOP_RENDERER = new IContraptionRenderer() {
        @Override
        public boolean isAvailable(Level level) {
            return false;
        }

        @Override
        public void renderContraption(Object contraption, PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {}

        @Override
        public void invalidate(Object contraption) {}
    };

    private static final AtomicReference<IContraptionRenderer> ACTIVE_RENDERER = new AtomicReference<>(NOOP_RENDERER);

    private ContraptionRenderDispatcher() {}

    /**
     * Registers or replaces the active contraption render engine.
     * By default, the platform loader registers the Flywheel backend.
     * Addons like Aeronautics can call this to supply their own optimized engine.
     */
    public static void registerRenderer(@NotNull IContraptionRenderer renderer) {
        Objects.requireNonNull(renderer, "renderer cannot be null");
        ACTIVE_RENDERER.set(renderer);
    }

    /**
     * Gets the currently active contraption renderer.
     */
    @NotNull
    public static IContraptionRenderer getRenderer() {
        return ACTIVE_RENDERER.get();
    }

    /**
     * Helper to render a contraption through the active dispatcher.
     */
    public static void render(Object contraption, Level level, PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {
        IContraptionRenderer renderer = ACTIVE_RENDERER.get();
        if (renderer.isAvailable(level)) {
            renderer.renderContraption(contraption, poseStack, buffers, light, partialTick);
        }
    }

    /**
     * Helper to invalidate a contraption across the active dispatcher.
     */
    public static void invalidate(Object contraption) {
        ACTIVE_RENDERER.get().invalidate(contraption);
    }
}

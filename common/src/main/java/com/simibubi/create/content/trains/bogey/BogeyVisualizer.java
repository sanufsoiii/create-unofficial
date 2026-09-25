package com.simibubi.create.content.trains.bogey;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface BogeyVisualizer {
	BogeyVisual createVisual(VisualizationContext ctx, float partialTick, boolean inContraption);
}

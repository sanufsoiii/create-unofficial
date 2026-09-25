package com.simibubi.create.foundation.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A set of widgets that are offset on the Z axis, allowing them to render above/below other "layers".
 */
@Environment(EnvType.CLIENT)
public class ScreenOverlay extends CompositeWidget {
	public final int zOffset;

	public ScreenOverlay(int zOffset) {
		this.zOffset = zOffset;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, this.zOffset);
		super.render(graphics, mouseX, mouseY, partialTicks);
		graphics.pose().popPose();
	}
}

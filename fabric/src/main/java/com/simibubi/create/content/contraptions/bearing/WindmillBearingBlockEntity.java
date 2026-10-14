package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;

public class WindmillBearingBlockEntity {

	public enum RotationDirection implements INamedIconOptions {
		CLOCKWISE(AllIcons.I_REFRESH),
		COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW);

		private final String translationKey;
		private final AllIcons icon;

		RotationDirection(AllIcons icon) {
			this.icon = icon;
			this.translationKey = "create.generic." + Lang.asId(name());
		}

		@Override
		public AllIcons getIcon() {
			return icon;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}
	}
}

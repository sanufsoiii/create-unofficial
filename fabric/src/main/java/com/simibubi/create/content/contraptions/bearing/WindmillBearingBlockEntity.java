package com.simibubi.create.content.contraptions.bearing;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WindmillBearingBlockEntity extends MechanicalBearingBlockEntity {

	public WindmillBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean isWindmill() {
		return true;
	}

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

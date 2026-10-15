package com.simibubi.create.foundation.ponder.element;

import java.util.function.Supplier;

import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.ParrotElementImpl;
import net.minecraft.world.phys.Vec3;

public class ExpandedParrotElement extends ParrotElementImpl {

	protected boolean deferConductor = false;

	protected ExpandedParrotElement(Vec3 location, Supplier<? extends ParrotPose> pose) {
		super(location, pose);
	}

	@Override
	public void reset(PonderScene scene) {
		super.reset(scene);
		if (entity != null)
			entity.removeTag("TrainHat");
		deferConductor = false;
	}

	@Override
	public void tick(PonderScene scene) {
		boolean wasNull = entity == null;
		super.tick(scene);
		if (wasNull) {
			if (deferConductor) {
				setConductor(true);
			}
			deferConductor = false;
		}
	}

	public void setConductor(boolean isConductor) {
		if (entity == null) {
			deferConductor = isConductor;
			return;
		}
		if (isConductor)
			entity.addTag("TrainHat");
		else
			entity.removeTag("TrainHat");
	}
}

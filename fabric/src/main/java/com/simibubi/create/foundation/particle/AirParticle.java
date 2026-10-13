package com.simibubi.create.foundation.particle;

import com.simibubi.create.Create;

import net.createmod.catnip.math.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class AirParticle extends SimpleAnimatedParticle {

	private float originX, originY, originZ;
	private float targetX, targetY, targetZ;
	private float drag;

	private float twirlRadius;
	private Axis twirlAxis;
	private float twirlAngleOffset;

	protected AirParticle(ClientLevel world, AirParticleData data, double x, double y, double z, double dx, double dy,
		double dz, SpriteSet sprite) {
		super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
		quadSize *= 0.75F;
		this.setPos(x, y, z);
		this.originX = (float) x;
		this.originY = (float) y;
		this.originZ = (float) z;
		this.targetX = (float) (x + dx);
		this.targetY = (float) (y + dy);
		this.targetZ = (float) (z + dz);
		this.drag = data.drag;

		twirlRadius = 0.15f + Create.RANDOM.nextFloat() * .15f;
		twirlAxis = Axis.Y;
		twirlAngleOffset = Create.RANDOM.nextFloat() * 360;

		Vec3 diff = new Vec3(dx, dy, dz);
		double length = diff.length();
		diff = diff.normalize();

		float longest = 0;
		for (Axis axis : Axis.values()) {
			float current = (float) Math.abs(diff.get(axis));
			if (current > longest) {
				longest = current;
				twirlAxis = axis;
			}
		}

		lifetime = Math.min((int) (length / data.speed), 60);
		selectSprite(7);
		setAlpha(.25f);

		if (length == 0) {
			remove();
			setAlpha(0);
		}
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		float progress = (float) Math.pow(((float) age) / lifetime, drag);
		float angle = (progress * 2 * 360 + twirlAngleOffset) % 360;
		Vec3 twirl = VecHelper.rotate(new Vec3(0, twirlRadius, 0), angle, twirlAxis);

		float x = (float) (Mth.lerp(progress, originX, targetX) + twirl.x);
		float y = (float) (Mth.lerp(progress, originY, targetY) + twirl.y);
		float z = (float) (Mth.lerp(progress, originZ, targetZ) + twirl.z);

		xd = x - this.x;
		yd = y - this.y;
		zd = z - this.z;

		move(xd, yd, zd);

		int sprites = 8;
		int step = lifetime / sprites;
		int current = age / step;
		selectSprite(current);
	}

	public int getLightColor(float p_189214_1_) {
		return 15 << 20 | 15 << 4;
	}

	private void selectSprite(int index) {
		setSprite(sprites.get(index, 8));
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<AirParticleData> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		@Override
		public Particle createParticle(AirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed,
			double ySpeed, double zSpeed) {
			return new AirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

}

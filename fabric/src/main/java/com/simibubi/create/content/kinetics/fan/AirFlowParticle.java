package com.simibubi.create.content.kinetics.fan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;

import net.createmod.catnip.math.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class AirFlowParticle extends SimpleAnimatedParticle {

	private final IAirCurrentSource source;
	private final Access access = new Access();

	protected AirFlowParticle(ClientLevel world, IAirCurrentSource source, double x, double y, double z,
							  SpriteSet sprite) {
		super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
		this.source = source;
		this.quadSize *= 0.75F;
		this.lifetime = 40;
		hasPhysics = false;
		selectSprite(7);
		Vec3 offset = VecHelper.offsetRandomly(Vec3.ZERO, random, .25f);
		this.setPos(x + offset.x, y + offset.y, z + offset.z);
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		setColor(0xEEEEEE);
		setAlpha(.25f);
	}

	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (source == null || source.isSourceRemoved()) {
			remove();
			return;
		}
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			remove();
		} else {
			AirCurrent airCurrent = source.getAirCurrent();
			if (airCurrent == null || airCurrent.direction == null) {
				remove();
				return;
			}

			Vec3 center = VecHelper.getCenterOf(source.getAirCurrentPos());
			Vec3 motion = Vec3.atLowerCornerOf(airCurrent.direction.getNormal());
			Vec3 distanceVec = new Vec3(x, y, z).subtract(center);
			double distance = distanceVec.length();

			if (distance > airCurrent.maxDistance) {
				remove();
				return;
			}

			if (distance != 0) {
				distanceVec = distanceVec.normalize();
				motion = motion.add(distanceVec.scale(1 / 16f));
			}

			motion = motion.scale(airCurrent.maxDistance - (distance - 1f)).scale(.5f);

			FanProcessingType type = getType(distance);
			if (type == null) {
				setColor(0xEEEEEE);
				setAlpha(.25f);
				selectSprite((int) Mth.clamp((distance / airCurrent.maxDistance) * 8 + random.nextInt(4),
						0, 7));
			} else {
				type.morphAirFlow(access, random);
			}

			xd = motion.x;
			yd = motion.y;
			zd = motion.z;

			move(xd, yd, zd);
		}
	}

	@Nullable
	private FanProcessingType getType(double distance) {
		if (source.getAirCurrent() == null)
			return null;
		return source.getAirCurrent().getTypeAt((float) distance);
	}

	public int getLightColor(float partialTick) {
		BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
		return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
	}

	private void selectSprite(int index) {
		setSprite(sprites.get(index, 8));
	}

	private class Access implements FanProcessingType.AirFlowParticleAccess {
		@Override
		public void setColor(int color) {
			AirFlowParticle.this.setColor(color);
		}

		@Override
		public void setAlpha(float alpha) {
			AirFlowParticle.this.setAlpha(alpha);
		}

		@Override
		public void spawnExtraParticle(ParticleOptions options, float speedMultiplier) {
			Vec3 m = Vec3.atLowerCornerOf(source.getAirFlowDirection().getNormal()).scale(speedMultiplier);
			AirFlowParticle.this.level.addParticle(options, x, y, z, m.x, m.y, m.z);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<AirFlowParticleData> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		@Override
		public Particle createParticle(AirFlowParticleData data, ClientLevel worldIn, double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			BlockEntity be = worldIn.getBlockEntity(new BlockPos(data.posX, data.posY, data.posZ));
			if (!(be instanceof IAirCurrentSource))
				be = null;
			return new AirFlowParticle(worldIn, (IAirCurrentSource) be, x, y, z, this.spriteSet);
		}
	}
}

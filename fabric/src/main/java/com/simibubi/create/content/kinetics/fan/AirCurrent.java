package com.simibubi.create.content.kinetics.fan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirCurrent {

	public final IAirCurrentSource source;
	public AABB bounds = new AABB(0, 0, 0, 0, 0, 0);
	public List<AirCurrentSegment> segments = new ArrayList<>();
	public Direction direction;
	public boolean pushing;
	public float maxDistance;

	protected List<Pair<TransportedItemStackHandlerBehaviour, FanProcessingType>> affectedItemHandlers =
		new ArrayList<>();
	protected List<Entity> caughtEntities = new ArrayList<>();

	public AirCurrent(IAirCurrentSource source) {
		this.source = source;
	}

	public void tick() {
		if (direction == null)
			rebuild();
		Level world = source.getAirCurrentWorld();
		Direction facing = direction;
		if (world != null && world.isClientSide) {
			float speed = source.getSpeed();
			if (speed != 0 && facing != null) {
				Vec3i vec = facing.getNormal();
				Vec3 out = new Vec3(vec.getX(), vec.getY(), vec.getZ());
				Vec3 center = VecHelper.getCenterOf(source.getAirCurrentPos());
				Vec3 pos = center.add(out.scale(.5f));
				world.addParticle(new AirFlowParticleData(source.getAirCurrentPos()), pos.x, pos.y, pos.z, 0, 0, 0);
			}
		}

		tickAffectedEntities(world);
		tickAffectedHandlers();
	}

	protected void tickAffectedEntities(Level world) {
		for (Iterator<Entity> iterator = caughtEntities.iterator(); iterator.hasNext(); ) {
			Entity entity = iterator.next();
			if (!entity.isAlive() || !entity.getBoundingBox()
				.intersects(bounds) || isPlayerCreativeFlying(entity)) {
				iterator.remove();
				continue;
			}

			Vec3 center = VecHelper.getCenterOf(source.getAirCurrentPos());
			Vec3i flow = (pushing ? direction : direction.getOpposite()).getNormal();

			float speed = Math.abs(source.getSpeed());
			float speedModifier = speed / 128f;
			float entityDistance = (float) entity.position()
				.distanceTo(center);
			float acceleration = (1 - entityDistance / maxDistance) * speedModifier;

			Vec3 previousMotion = entity.getDeltaMovement();
			float maxAcceleration = 5;

			double xIn = Mth.clamp(flow.getX() * acceleration - previousMotion.x, -maxAcceleration, maxAcceleration);
			double yIn = Mth.clamp(flow.getY() * acceleration - previousMotion.y, -maxAcceleration, maxAcceleration);
			double zIn = Mth.clamp(flow.getZ() * acceleration - previousMotion.z, -maxAcceleration, maxAcceleration);

			entity.setDeltaMovement(previousMotion.add(new Vec3(xIn, yIn, zIn).scale(1 / 8f)));
			entity.fallDistance = 0;
			if (CatnipServices.PLATFORM.getEnv().isClient()) {
				Client.enableClientPlayerSound(entity, Mth.clamp(speed / 128f * .4f, 0.01f, .4f));
			}

			if (entity instanceof ServerPlayer)
				((ServerPlayer) entity).connection.aboveGroundTickCount = 0;

			FanProcessingType processingType = getTypeAt((float) entityDistance);

			if (processingType == null)
				continue;

			if (entity instanceof ItemEntity itemEntity) {
				if (world.isClientSide) {
					FanProcessing.spawnParticlesForProcessing(world, entity.position(), processingType);
					continue;
				}
				if (FanProcessing.canProcess(itemEntity, processingType))
					if (FanProcessing.applyProcessing(itemEntity, processingType))
						continue;
			}

			if (world.isClientSide)
				continue;

			processingType.affectEntity(entity, world);
		}
	}

	public static boolean isPlayerCreativeFlying(Entity entity) {
		if (entity instanceof Player player) {
			return player.isCreative() && player.getAbilities().flying;
		}
		return false;
	}

	public void tickAffectedHandlers() {
		for (Pair<TransportedItemStackHandlerBehaviour, FanProcessingType> pair : affectedItemHandlers) {
			TransportedItemStackHandlerBehaviour handler = pair.getKey();
			Level world = handler.getWorld();
			FanProcessingType processingType = pair.getRight();
			if (processingType == null)
				continue;

			handler.handleProcessingOnAllItems(transported -> {
				if (world.isClientSide) {
					FanProcessing.spawnParticlesForProcessing(world, handler.getWorldPositionOf(transported),
						processingType);
					return TransportedResult.doNothing();
				}
				return FanProcessing.applyProcessing(transported, world, processingType);
			});
		}
	}

	public void rebuild() {
		if (source.getSpeed() == 0) {
			maxDistance = 0;
			segments.clear();
			bounds = new AABB(0, 0, 0, 0, 0, 0);
			return;
		}

		direction = source.getAirflowOriginSide();
		pushing = source.getAirFlowDirection() == direction;
		maxDistance = source.getMaxDistance();

		Level world = source.getAirCurrentWorld();
		BlockPos start = source.getAirCurrentPos();
		float max = maxDistance;
		Direction facing = direction;
		Vec3 directionVec = Vec3.atLowerCornerOf(facing.getNormal());
		maxDistance = getFlowLimit(world, start, max, facing);

		// Determine segments with transported fluids/gases
		segments.clear();
		AirCurrentSegment currentSegment = null;
		FanProcessingType type = null;

		int limit = getLimit();
		int searchStart = pushing ? 1 : limit;
		int searchEnd = pushing ? limit : 1;
		int searchStep = pushing ? 1 : -1;
		int segmentStart = Math.min(searchStart, searchEnd);

		for (int i = searchStart; i * searchStep <= searchEnd * searchStep; i += searchStep) {
			BlockPos currentPos = start.relative(direction, i);
			FanProcessingType newType = FanProcessingType.getAt(world, currentPos);
			if (newType != null) {
				type = newType;
			}
			if (currentSegment == null) {
				currentSegment = new AirCurrentSegment();
				currentSegment.startOffset = segmentStart;
				currentSegment.type = type;
			} else if (currentSegment.type != type) {
				currentSegment.endOffset = i - searchStep;
				segments.add(currentSegment);
				currentSegment = new AirCurrentSegment();
				currentSegment.startOffset = i;
				currentSegment.type = type;
			}
		}
		if (currentSegment != null) {
			currentSegment.endOffset = searchEnd;
			segments.add(currentSegment);
		}

		// Build Bounding Box
		if (maxDistance < 0.25f)
			bounds = new AABB(0, 0, 0, 0, 0, 0);
		else {
			float offset = pushing ? 0 : -maxDistance;
			Vec3 center = VecHelper.getCenterOf(start);
			Vec3 corner = center.add(directionVec.scale(offset));
			Vec3 oppositeCorner = corner.add(directionVec.scale(maxDistance));
			bounds = new AABB(corner, oppositeCorner).inflate(1)
				.expandTowards(directionVec.scale(pushing ? 1 : -1));
		}

		findEntities();
		findHandlers();
	}

	public static float getFlowLimit(Level world, BlockPos start, float max, Direction facing) {
		if (world == null)
			return 0;

		for (int i = 0; i < max; i++) {
			BlockPos currentPos = start.relative(facing, i + 1);
			if (!world.isLoaded(currentPos)) {
				return i;
			}

			BlockState state = world.getBlockState(currentPos);
			if (shouldAlwaysPass(state)) {
				continue;
			}

			VoxelShape shape = state.getCollisionShape(world, currentPos);
			if (shape.isEmpty()) {
				continue;
			}
			if (shape == Shapes.block()) {
				return i;
			}
			double shapeDepth = findMaxDepth(shape, facing);
			if (shapeDepth == Double.POSITIVE_INFINITY) {
				continue;
			}
			return Math.min((float) (i + shapeDepth + 1 / 32d), max);
		}

		return max;
	}

	private static final double[][] DEPTH_TEST_COORDINATES = {
		{0.25, 0.25},
		{0.25, 0.75},
		{0.5, 0.5},
		{0.75, 0.25},
		{0.75, 0.75}
	};

	// Finds the maximum depth of the shape when traveling in the given direction.
	// Infinity represents that the shape does not block the air current at all.
	private static double findMaxDepth(VoxelShape shape, Direction direction) {
		Direction.Axis axis = direction.getAxis();
		Direction.AxisDirection axisDirection = direction.getAxisDirection();
		Direction.Axis uAxis = axis == Direction.Axis.X ? Direction.Axis.Y : Direction.Axis.X;
		Direction.Axis vAxis = axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.Z;

		double maxDepth = Double.NEGATIVE_INFINITY;

		for (double[] coordinates : DEPTH_TEST_COORDINATES) {
			double u = coordinates[0];
			double v = coordinates[1];
			List<Double> depths = new ArrayList<>();
			shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
				double minU = getValueOnAxis(minX, minY, minZ, uAxis);
				double maxU = getValueOnAxis(maxX, maxY, maxZ, uAxis);
				double minV = getValueOnAxis(minX, minY, minZ, vAxis);
				double maxV = getValueOnAxis(maxX, maxY, maxZ, vAxis);
				if (u >= minU && u <= maxU && v >= minV && v <= maxV) {
					double minD = getValueOnAxis(minX, minY, minZ, axis);
					double maxD = getValueOnAxis(maxX, maxY, maxZ, axis);
					double depth = axisDirection == Direction.AxisDirection.POSITIVE ? minD : 1 - maxD;
					depths.add(depth);
				}
			});

			if (depths.isEmpty()) {
				return Double.POSITIVE_INFINITY;
			}

			for (double depth : depths) {
				maxDepth = Math.max(maxDepth, depth);
			}
		}

		return maxDepth;
	}

	private static double getValueOnAxis(double x, double y, double z, Direction.Axis axis) {
		return switch (axis) {
			case X -> x;
			case Y -> y;
			case Z -> z;
		};
	}

	private static boolean shouldAlwaysPass(BlockState state) {
		return AllTags.AllBlockTags.FAN_TRANSPARENT.matches(state);
	}

	public void findHandlers() {
		Level world = source.getAirCurrentWorld();
		BlockPos start = source.getAirCurrentPos();
		affectedItemHandlers.clear();
		int limit = getLimit();

		for (int i = 1; i <= limit; i++) {
			FanProcessingType segmentType = getTypeAt(i - 1);
			for (int offset : Iterate.zeroAndOne) {
				BlockPos pos = start.relative(direction, i)
					.below(offset);
				TransportedItemStackHandlerBehaviour behaviour =
					BlockEntityBehaviour.get(world, pos, TransportedItemStackHandlerBehaviour.TYPE);
				if (behaviour != null) {
					FanProcessingType type = FanProcessingType.getAt(world, pos);
					if (type == null)
						type = segmentType;
					affectedItemHandlers.add(Pair.of(behaviour, type));
				}
				if (direction.getAxis()
					.isVertical())
					break;
			}
		}
	}

	public void findEntities() {
		caughtEntities.clear();
		caughtEntities = source.getAirCurrentWorld()
			.getEntities(null, bounds);
	}

	@Nullable
	public FanProcessingType getTypeAt(float offset) {
		if (offset >= 0 && offset <= maxDistance) {
			if (pushing) {
				for (AirCurrentSegment segment : segments) {
					if (offset <= segment.endOffset) {
						return segment.type;
					}
				}
			} else {
				for (AirCurrentSegment segment : segments) {
					if (offset >= segment.startOffset) {
						return segment.type;
					}
				}
			}
		}
		return null;
	}

	private static class AirCurrentSegment {
		@Nullable
		private FanProcessingType type;
		private int startOffset;
		private int endOffset;
	}

	private int getLimit() {
		return (int) (maxDistance + (pushing ? 1 : 0));
	}

	@Environment(EnvType.CLIENT)
	public static class Client {
		private static boolean isClientPlayerInAirCurrent;

		private static AirCurrentSound flyingSound;

		private static void enableClientPlayerSound(Entity e, float maxVolume) {
			if (e != Minecraft.getInstance()
					.getCameraEntity())
				return;

			isClientPlayerInAirCurrent = true;

			float pitch = (float) Mth.clamp(e.getDeltaMovement()
					.length() * .5f, .5f, 2f);

			if (flyingSound == null || flyingSound.isStopped()) {
				flyingSound = new AirCurrentSound(SoundEvents.ELYTRA_FLYING, pitch);
				Minecraft.getInstance()
						.getSoundManager()
						.play(flyingSound);
			}
			flyingSound.setPitch(pitch);
			flyingSound.fadeIn(maxVolume);
		}

		public static void tickClientPlayerSounds() {
			if (!isClientPlayerInAirCurrent && flyingSound != null)
				if (flyingSound.isFaded())
					flyingSound.stopSound();
				else
					flyingSound.fadeOut();
			isClientPlayerInAirCurrent = false;
		}
	}
}

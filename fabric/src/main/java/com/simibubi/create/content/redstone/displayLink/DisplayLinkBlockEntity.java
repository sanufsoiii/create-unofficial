package com.simibubi.create.content.redstone.displayLink;

import java.util.List;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DisplayLinkBlockEntity extends LinkWithBulbBlockEntity implements TransformableBlockEntity {

	protected BlockPos targetOffset;
	public int targetLine;
	public int refreshTicks;
	private CompoundTag sourceConfig;

	public DisplayLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		targetOffset = BlockPos.ZERO;
		sourceConfig = new CompoundTag();
		targetLine = 0;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new AdvancementBehaviour(this));
	}

	@Override
	public void tick() {
		super.tick();

		if (isVirtual())
			return;
		if (level == null || level.isClientSide)
			return;

		refreshTicks++;
		if (refreshTicks >= 100) {
			tickSource();
		}
	}

	public void tickSource() {
		refreshTicks = 0;
		if (getBlockState().getOptionalValue(DisplayLinkBlock.POWERED).orElse(true))
			return;
		if (level != null && !level.isClientSide)
			updateGatheredData();
	}

	public void onNoLongerPowered() {
		refreshTicks = 0;
		updateGatheredData();
	}

	public void updateGatheredData() {
		BlockPos sourcePosition = getSourcePosition();
		BlockPos targetPosition = getTargetPosition();

		if (level == null || !level.isLoaded(targetPosition) || !level.isLoaded(sourcePosition))
			return;

		sendPulseNextSync();
		sendData();
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		writeGatheredData(tag);
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		writeGatheredData(tag);
	}

	private void writeGatheredData(CompoundTag tag) {
		tag.put("TargetOffset", NbtUtils.writeBlockPos(targetOffset));
		tag.putInt("TargetLine", targetLine);
		if (!sourceConfig.isEmpty()) {
			tag.put("Source", sourceConfig.copy());
		}
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		targetOffset = NBTHelper.readBlockPos(tag, "TargetOffset");
		targetLine = tag.getInt("TargetLine");
		if (tag.contains("Source")) {
			sourceConfig = tag.getCompound("Source").copy();
		}
	}

	public void target(BlockPos targetPosition) {
		this.targetOffset = targetPosition.subtract(worldPosition);
	}

	public BlockPos getSourcePosition() {
		return worldPosition.relative(getDirection());
	}

	public CompoundTag getSourceConfig() {
		return sourceConfig;
	}

	public void setSourceConfig(CompoundTag sourceConfig) {
		this.sourceConfig = sourceConfig;
	}

	public Direction getDirection() {
		return getBlockState().getOptionalValue(DisplayLinkBlock.FACING)
			.orElse(Direction.UP)
			.getOpposite();
	}

	public BlockPos getTargetPosition() {
		return worldPosition.offset(targetOffset);
	}

	private static final Vec3 bulbOffset = VecHelper.voxelSpace(11, 7, 5);
	private static final Vec3 bulbOffsetVertical = VecHelper.voxelSpace(5, 7, 11);

	@Override
	public Vec3 getBulbOffset(BlockState state) {
		if (state.getOptionalValue(DisplayLinkBlock.FACING).orElse(Direction.UP).getAxis().isVertical())
			return bulbOffsetVertical;
		return bulbOffset;
	}

	@Override
	public void transform(BlockEntity be, StructureTransform transform) {
		targetOffset = transform.applyWithoutOffset(targetOffset);
		notifyUpdate();
	}

}

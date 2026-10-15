package com.simibubi.create.infrastructure.ponder.scenes.highLogistics;

import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PonderHilo {

	public static void packagerCreate(CreateSceneBuilder scene, BlockPos pos, ItemStack box) {
		scene.world()
			.modifyBlockEntity(pos, PackagerBlockEntity.class, be -> {
				be.animationTicks = PackagerBlockEntity.CYCLE;
				be.animationInward = false;
				be.heldBox = box;
			});
	}

	public static void packagerUnpack(CreateSceneBuilder scene, BlockPos pos, ItemStack box) {
		scene.world()
			.modifyBlockEntity(pos, PackagerBlockEntity.class, be -> {
				be.animationTicks = PackagerBlockEntity.CYCLE;
				be.animationInward = true;
				be.previouslyUnwrapped = box;
			});
	}

	public static void packagerClear(CreateSceneBuilder scene, BlockPos pos) {
		scene.world()
			.modifyBlockEntity(pos, PackagerBlockEntity.class, be -> be.heldBox = ItemStack.EMPTY);
	}

	public static ElementLink<EntityElement> packageHopsOffBelt(CreateSceneBuilder scene, BlockPos beltPos,
		Direction side, ItemStack box) {
		scene.world()
			.removeItemsFromBelt(beltPos);
		return scene.world()
			.createEntity(l -> {
				ItemEntity itemEntity = new ItemEntity(l, beltPos.getX() + 0.5 + side.getStepX() * 0.675,
					beltPos.getY() + 0.875, beltPos.getZ() + 0.5 + side.getStepZ() * 0.675, box);
				itemEntity.setDeltaMovement(new Vec3(side.getStepX(), 1f, side.getStepZ()).scale(0.125f));
				return itemEntity;
			});
	}

	public static void linkEffect(CreateSceneBuilder scene, BlockPos pos) {
		scene.world()
			.flashDisplayLink(pos);
		scene.addInstruction(s -> {
			Vec3 vec3 = Vec3.atCenterOf(pos);
			s.getWorld()
				.addParticle(ParticleTypes.HAPPY_VILLAGER, vec3.x, vec3.y, vec3.z, 0, 0, 0);
		});
	}

	public static void requesterEffect(CreateSceneBuilder scene, BlockPos pos) {
		scene.addInstruction(s -> {
			Vec3 vec3 = Vec3.atCenterOf(pos);
			s.getWorld()
				.addParticle(ParticleTypes.HAPPY_VILLAGER, vec3.x, vec3.y, vec3.z, 0, 0, 0);
		});
	}

}

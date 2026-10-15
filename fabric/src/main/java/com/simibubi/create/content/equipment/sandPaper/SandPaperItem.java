package com.simibubi.create.content.equipment.sandPaper;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SandPaperItem extends Item implements CustomUseEffectsItem {

	public SandPaperItem(Properties properties) {
		super(properties.durability(8));
	}

	public static void spawnParticles(Vec3 location, ItemStack polishedStack, Level world) {
		for (int i = 0; i < 20; i++) {
			Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, 1 / 8f);
			world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, polishedStack), location.x, location.y,
				location.z, motion.x, motion.y, motion.z);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		if (itemstack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.consume(itemstack);
		}
		return InteractionResultHolder.fail(itemstack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		if (!(entityLiving instanceof Player player))
			return stack;
		if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
			ItemStack toPolish = stack.get(AllDataComponents.SAND_PAPER_POLISHING).item();
			Inventory playerInv = player.getInventory();
			playerInv.placeItemBackInInventory(toPolish);
			stack.remove(AllDataComponents.SAND_PAPER_POLISHING);
			stack.hurtAndBreak(1, entityLiving, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
		}
		return stack;
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof Player player))
			return;
		if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
			ItemStack toPolish = stack.get(AllDataComponents.SAND_PAPER_POLISHING).item();
			player.getInventory().placeItemBackInInventory(toPolish);
			stack.remove(AllDataComponents.SAND_PAPER_POLISHING);
		}
	}

	@Override
	public SoundEvent getEatingSound() {
		return AllSoundEvents.SANDING_SHORT.getMainEvent();
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 32;
	}

	@Override
	public int getEnchantmentValue() {
		return 1;
	}

	@Override
	public net.createmod.catnip.data.TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
		return net.createmod.catnip.data.TriState.TRUE;
	}

	@Override
	public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, net.minecraft.util.RandomSource random) {
		if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
			ItemStack polishing = stack.get(AllDataComponents.SAND_PAPER_POLISHING).item();
			if (!polishing.isEmpty())
				spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), polishing, entity.level());
		}

		if ((entity.getTicksUsingItem() - 6) % 7 == 0)
			entity.playSound(entity.getEatingSound(stack), 0.9F + 0.2F * random.nextFloat(),
				random.nextFloat() * 0.2F + 0.9F);

		return true;
	}
}

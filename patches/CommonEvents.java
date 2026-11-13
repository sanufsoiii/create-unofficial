package com.simibubi.create.foundation.events;

import com.mojang.brigadier.CommandDispatcher;
import com.simibubi.create.Create;
import com.simibubi.create.api.event.PipeCollisionEvent;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsServerHandler;
import com.simibubi.create.content.contraptions.glue.SuperGlueHandler;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.CouplingPhysics;
import com.simibubi.create.content.contraptions.minecart.MinecartCouplingItem;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.equipment.armor.CardboardArmorHandler;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.armor.NetheriteDivingHandler;
import com.simibubi.create.content.equipment.bell.HauntedBellPulser;
import com.simibubi.create.content.equipment.clipboard.ClipboardValueSettingsHandler;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileTypeManager;
import com.simibubi.create.content.equipment.potatoCannon.PotatoProjectileTypeManager.ReloadListener;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryHandler;
import com.simibubi.create.content.equipment.tool.CardboardSwordItem;
import com.simibubi.create.content.equipment.toolbox.ToolboxHandler;
import com.simibubi.create.content.equipment.wrench.WrenchEventHandler;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.equipment.zapper.ZapperInteractionHandler;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.content.fluids.FluidBottleItemHook;
import com.simibubi.create.content.fluids.FluidReactions;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ServerChainConveyorHandler;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerHandler;
import com.simibubi.create.content.redstone.link.LinkHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerServerHandler;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import com.simibubi.create.content.trains.schedule.ScheduleItemEntityInteraction;
import com.simibubi.create.foundation.block.ItemUseOverrides;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsInputHandler;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionHandler;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.WorldAttached;
import com.simibubi.create.infrastructure.command.AllCommands;
import io.github.fabricators_of_create.porting_lib_ufo.PortingLibBase;
import io.github.fabricators_of_create.porting_lib_ufo.entity.events.EntityDataEvents;
import io.github.fabricators_of_create.porting_lib_ufo.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib_ufo.entity.events.EntityMountEvents;
import io.github.fabricators_of_create.porting_lib_ufo.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib_ufo.event.common.BlockEvents;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1936;
import net.minecraft.class_1937;
import net.minecraft.class_2168;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3264;
import net.minecraft.class_3966;
import net.minecraft.class_7157;
import net.minecraft.class_2170.class_5364;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class CommonEvents {
   public static void onServerTick(MinecraftServer server) {
      Create.SCHEMATIC_RECEIVER.tick();
      Create.LAGGER.tick();
      ServerSpeedProvider.serverTick(server);
      Create.RAILWAYS.sync.serverTick();
      ServerChainConveyorHandler.tick();
   }

   public static void onChunkUnloaded(class_1937 world, class_2818 chunk) {
      CapabilityMinecartController.onChunkUnloaded(world, chunk);
   }

   public static void playerLoggedIn(class_3244 handler, PacketSender sender, MinecraftServer server) {
      ToolboxHandler.playerLogin(handler.method_32311());
      Create.RAILWAYS.playerLogin(handler.method_32311());
   }

   public static void playerLoggedOut(class_3244 handler, MinecraftServer server) {
      class_1657 player = handler.method_32311();
      Create.RAILWAYS.playerLogout(player);
   }

   public static void onServerWorldTick(class_1937 world) {
      if (!world.method_8608()) {
         ContraptionHandler.tick(world);
         CapabilityMinecartController.tick(world);
         CouplingPhysics.tick(world);
         LinkedControllerServerHandler.tick(world);
         ControlsServerHandler.tick(world);
         Create.RAILWAYS.tick(world);
      }
   }

   public static void onUpdateLivingEntity(class_1309 entityLiving) {
      class_1937 world = entityLiving.method_37908();
      if (world != null) {
         ContraptionHandler.entitiesWhoJustDismountedGetSentToTheRightLocation(entityLiving, world);
         ToolboxHandler.entityTick(entityLiving, world);
      }
   }

   public static void onEntityAdded(class_1297 entity, class_1937 world) {
      ContraptionHandler.addSpawnedContraptionsToCollisionList(entity, world);
   }

   public static class_1269 onEntityAttackedByPlayer(
      class_1657 playerEntity, class_1937 world, class_1268 hand, class_1297 entity, @Nullable class_3966 entityRayTraceResult
   ) {
      return WrenchItem.wrenchInstaKillsMinecarts(playerEntity, world, hand, entity, entityRayTraceResult);
   }

   public static void registerCommands(CommandDispatcher<class_2168> dispatcher, class_7157 registryAccess, class_5364 environment) {
      AllCommands.register(dispatcher);
   }

   public static void onEntityEnterSection(class_1297 entity, long packedOldPos, long packedNewPos) {
      CarriageEntityHandler.onEntityEnterSection(entity, packedOldPos, packedNewPos);
   }

   public static void addReloadListeners() {
      ResourceManagerHelper.get(class_3264.field_14190).registerReloadListener(RecipeFinder.LISTENER);
      ResourceManagerHelper.get(class_3264.field_14190).registerReloadListener(ReloadListener.INSTANCE);
      ResourceManagerHelper.get(class_3264.field_14190).registerReloadListener(BeltHelper.LISTENER);
   }

   public static void onDatapackSync(class_3222 player, boolean joined) {
      PotatoProjectileTypeManager.syncTo(player);
   }

   public static void serverStopping(MinecraftServer server) {
      Create.SCHEMATIC_RECEIVER.shutdown();
   }

   public static void onLoadWorld(Executor executor, class_1936 world) {
      if (world instanceof class_3218 level) {
         Create.serverLevel = level;
         PortingLibBase.serverLevel = level;
      }

      Create.REDSTONE_LINK_NETWORK_HANDLER.onLoadWorld(world);
      Create.TORQUE_PROPAGATOR.onLoadWorld(world);
      Create.RAILWAYS.levelLoaded(world);
   }

   public static void onUnloadWorld(Executor executor, class_1936 world) {
      Create.REDSTONE_LINK_NETWORK_HANDLER.onUnloadWorld(world);
      Create.TORQUE_PROPAGATOR.onUnloadWorld(world);
      WorldAttached.invalidateWorld(world);
   }

   public static void startTracking(class_1297 target, class_3222 player) {
      CapabilityMinecartController.startTracking(target);
   }

   public static void leftClickEmpty(class_3222 player) {
      class_1799 stack = player.method_6047();
      if (stack.method_7909() instanceof ZapperItem) {
         ZapperInteractionHandler.trySelect(stack, player);
      }
   }

   public static void addPackFinders() {
      FabricLoader.getInstance().getModContainer("create").ifPresent(create -> {
         if (create.findPath("resourcepacks/legacy_copper").isPresent()) {
            class_2960 packId = Create.asResource("legacy_copper");
            ResourceManagerHelper.registerBuiltinResourcePack(packId, create, "Create Legacy Copper", ResourcePackActivationType.NORMAL);
         }
      });
   }

   public static void register() {
      ServerTickEvents.END_SERVER_TICK.register(CommonEvents::onServerTick);
      ServerChunkEvents.CHUNK_UNLOAD.register(CommonEvents::onChunkUnloaded);
      ServerTickEvents.END_WORLD_TICK.register(CommonEvents::onServerWorldTick);
      ServerEntityEvents.ENTITY_LOAD.register(CommonEvents::onEntityAdded);
      ServerLifecycleEvents.SERVER_STOPPED.register(CommonEvents::serverStopping);
      ServerWorldEvents.LOAD.register(CommonEvents::onLoadWorld);
      ServerWorldEvents.UNLOAD.register(CommonEvents::onUnloadWorld);
      ServerPlayConnectionEvents.DISCONNECT.register(CommonEvents::playerLoggedOut);
      AttackEntityCallback.EVENT.register(CommonEvents::onEntityAttackedByPlayer);
      CommandRegistrationCallback.EVENT.register(CommonEvents::registerCommands);
      EntityEvents.START_TRACKING_TAIL.register(CommonEvents::startTracking);
      EntityEvents.ENTERING_SECTION.register(CommonEvents::onEntityEnterSection);
      LivingEntityEvents.TICK.register(CommonEvents::onUpdateLivingEntity);
      ServerPlayConnectionEvents.JOIN.register(CommonEvents::playerLoggedIn);
      ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(CommonEvents::onDatapackSync);
      PipeCollisionEvent.FLOW.register(FluidReactions::handlePipeFlowCollisionFallback);
      PipeCollisionEvent.SPILL.register(FluidReactions::handlePipeSpillCollisionFallback);
      addReloadListeners();
      addPackFinders();
      UseEntityCallback.EVENT.register(MinecartCouplingItem::handleInteractionWithMinecart);
      UseEntityCallback.EVENT.register(MinecartContraptionItem::wrenchCanBeUsedToPickUpMinecartContraptions);
      UseBlockCallback.EVENT.register(WrenchEventHandler::useOwnWrenchLogicForCreateBlocks);
      UseBlockCallback.EVENT.register(LinkHandler::onBlockActivated);
      UseBlockCallback.EVENT.register(ItemUseOverrides::onBlockActivated);
      UseBlockCallback.EVENT.register(EdgeInteractionHandler::onBlockActivated);
      UseBlockCallback.EVENT.register(FluidBottleItemHook::preventWaterBottlesFromCreatesFluids);
      UseBlockCallback.EVENT.register(SuperGlueItem::glueItemAlwaysPlacesWhenUsed);
      UseBlockCallback.EVENT.register(ManualApplicationRecipe::manualApplicationRecipesApplyInWorld);
      UseBlockCallback.EVENT.register(ValueSettingsInputHandler::onBlockActivated);
      UseBlockCallback.EVENT.register(ValveHandleBlock::onBlockActivated);
      UseBlockCallback.EVENT.register(ClipboardValueSettingsHandler::rightClickToCopy);
      // ChainConveyorConnectionHandler is a client-only class (@Environment(CLIENT)); registering it here
      // made dedicated servers fail to start. On the client it is still hooked up.
      if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
         UseBlockCallback.EVENT.register(ChainConveyorConnectionHandler::onItemUsedOnBlock);
      }
      AttackBlockCallback.EVENT.register(ClipboardValueSettingsHandler::leftClickToPaste);
      AttackBlockCallback.EVENT.register(ZapperInteractionHandler::leftClickingBlocksWithTheZapperSelectsTheBlock);
      UseEntityCallback.EVENT.register(ScheduleItemEntityInteraction::interactWithConductor);
      ServerTickEvents.END_WORLD_TICK.register(HauntedBellPulser::hauntedBellCreatesPulse);
      LivingEntityEvents.ATTACK.register(DeployerFakePlayer::entitiesDontRetaliate);
      EntityMountEvents.MOUNT.register(CouplingHandler::preventEntitiesFromMoutingOccupiedCart);
      LivingEntityEvents.EXPERIENCE_DROP.register(DeployerFakePlayer::deployerKillsDoNotSpawnXP);
      LivingEntityEvents.HURT.register(ExtendoGripItem::bufferLivingAttackEvent);
      LivingEntityEvents.KNOCKBACK_STRENGTH.register(ExtendoGripItem::attacksByExtendoGripHaveMoreKnockback);
      LivingEntityEvents.TICK.register(ExtendoGripItem::holdingExtendoGripIncreasesRange);
      LivingEntityEvents.TICK.register(DivingBootsItem::accellerateDescentUnderwater);
      LivingEntityEvents.TICK.register(DivingHelmetItem::breatheUnderwater);
      LivingEntityEvents.DROPS.register(CrushingWheelBlockEntity::handleCrushedMobDrops);
      LivingEntityEvents.LOOTING_LEVEL.register(CrushingWheelBlockEntity::crushingIsFortunate);
      LivingEntityEvents.DROPS.register(DeployerFakePlayer::deployerCollectsDropsFromKilledEntities);
      ServerEntityEvents.EQUIPMENT_CHANGE.register(NetheriteDivingHandler::onLivingEquipmentChange);
      EntityEvents.SIZE.register(DeployerFakePlayer::deployerHasEyesOnHisFeet);
      BlockEvents.POST_PROCESS_PLACE.register(SymmetryHandler::onBlockPlaced);
      BlockEvents.POST_PROCESS_PLACE.register(SuperGlueHandler::glueListensForBlockPlacement);
      EntityEvents.PROJECTILE_IMPACT.register(BlazeBurnerHandler::onThrowableImpact);
      EntityDataEvents.LOAD.register(ExtendoGripItem::addReachToJoiningPlayersHoldingExtendo);
      PlayerBlockBreakEvents.BEFORE.register(SymmetryHandler::onBlockDestroyed);
      CardboardArmorHandler.register();
      CardboardSwordItem.register();
   }
}

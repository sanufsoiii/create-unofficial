package com.simibubi.create.foundation.data;

import java.util.function.Supplier;

import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogCTBehaviour;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BuilderTransformers {

	public static <B extends EncasedShaftBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedShaft(
		String casing, Supplier<CTSpriteShiftEntry> casingShift) {
		return builder -> builder.initialProperties(SharedProperties::stone)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(CStress.setNoImpact())
			.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(casingShift.get())))
			.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
				(s, f) -> f.getAxis() != s.getValue(EncasedShaftBlock.AXIS))))
			.simpleItem();
	}

	public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCogwheel(
		String casing, Supplier<CTSpriteShiftEntry> casingShift) {
		return builder -> builder.initialProperties(SharedProperties::stone)
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(CStress.setNoImpact())
			.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, casingShift.get(),
				(s, f) -> f.getAxis() == s.getValue(EncasedCogwheelBlock.AXIS)
					&& !s.getValue(f.getAxisDirection() == AxisDirection.POSITIVE ? EncasedCogwheelBlock.TOP_SHAFT
					: EncasedCogwheelBlock.BOTTOM_SHAFT))))
			.simpleItem();
	}

	public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedLargeCogwheel(
		String casing, Supplier<CTSpriteShiftEntry> casingShift) {
		return builder -> BuilderTransformers.<B, P>encasedCogwheel(casing, casingShift).apply(builder)
			.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCogCTBehaviour(casingShift.get())));
	}

	public static <B extends com.simibubi.create.content.decoration.encasing.CasingBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> casing(
		Supplier<CTSpriteShiftEntry> ct) {
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.sound(net.minecraft.world.level.block.SoundType.WOOD))
			.transform(TagGen.axeOrPickaxe())
			.onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
			.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
			.tag(com.simibubi.create.AllTags.AllBlockTags.CASING.tag)
			.item()
			.tag(com.simibubi.create.AllTags.AllItemTags.CASING.tag)
			.build();
	}

	public static <B extends com.simibubi.create.content.decoration.MetalScaffoldingBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> scaffold(
		net.minecraft.world.level.material.MapColor color, CTSpriteShiftEntry scaffoldShift,
		CTSpriteShiftEntry scaffoldInsideShift, CTSpriteShiftEntry casingShift) {
		return b -> b.initialProperties(() -> net.minecraft.world.level.block.Blocks.SCAFFOLDING)
			.properties(p -> p.sound(net.minecraft.world.level.block.SoundType.COPPER).mapColor(color))
			.onRegister(CreateRegistrate.connectedTextures(
				() -> new com.simibubi.create.content.decoration.MetalScaffoldingCTBehaviour(scaffoldShift, scaffoldInsideShift, casingShift)))
			.transform(TagGen.pickaxeOnly())
			.tag(net.minecraft.tags.BlockTags.CLIMBABLE)
			.item(com.simibubi.create.content.decoration.MetalScaffoldingBlockItem::new)
			.build();
	}

	public static <B extends net.minecraft.world.level.block.IronBarsBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> metalBars(
		net.minecraft.world.level.material.MapColor color) {
		return b -> b.initialProperties(() -> net.minecraft.world.level.block.Blocks.IRON_BARS)
			.properties(p -> p.sound(net.minecraft.world.level.block.SoundType.COPPER).mapColor(color))
			.tag(com.simibubi.create.AllTags.AllBlockTags.WRENCH_PICKUP.tag)
			.tag(com.simibubi.create.AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
			.transform(TagGen.pickaxeOnly())
			.simpleItem();
	}

	public static <B extends net.minecraft.world.level.block.Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> palettesIronBlock() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.mapColor(net.minecraft.world.level.material.MapColor.COLOR_GRAY)
				.sound(net.minecraft.world.level.block.SoundType.NETHERITE_BLOCK)
				.requiresCorrectToolForDrops())
			.transform(TagGen.pickaxeOnly())
			.tag(com.simibubi.create.AllTags.AllBlockTags.WRENCH_PICKUP.tag)
			.simpleItem();
	}

	public static <B extends com.simibubi.create.content.decoration.MetalLadderBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> ladder(
		net.minecraft.world.level.material.MapColor color) {
		return b -> b.initialProperties(() -> net.minecraft.world.level.block.Blocks.LADDER)
			.properties(p -> p.sound(net.minecraft.world.level.block.SoundType.COPPER).mapColor(color))
			.transform(TagGen.pickaxeOnly())
			.tag(net.minecraft.tags.BlockTags.CLIMBABLE)
			.simpleItem();
	}

	public static <B extends net.minecraft.world.level.block.Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> bell() {
		return b -> b.initialProperties(SharedProperties::softMetal)
			.properties(p -> p.noOcclusion()
				.sound(net.minecraft.world.level.block.SoundType.ANVIL))
			.transform(TagGen.pickaxeOnly())
			.tag(com.simibubi.create.AllTags.AllBlockTags.BRITTLE.tag)
			.item()
			.tag(com.simibubi.create.AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
			.build();
	}
}


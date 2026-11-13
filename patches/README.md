# 6.0.10.1 hotfix sources

The 6.0.10.1 release is the published 6.0.10 jar with these classes recompiled (decompiled from that jar,
intermediary names) because the current source tree differs from what was published:

- `KnockbackPacket` + `KnockbackPacketClient`: client-only code moved out so dedicated servers can load `AllPackets`.
- `CommonEvents`: the client-only `ChainConveyorConnectionHandler` is only registered on the client.
- `GuiGameElement`: GUI block previews (JEI sequenced assembly etc.) render at full brightness instead of black.

# 6.0.10.2

- `GuiGameElement`: a block with no baked model (some mods/model loaders in large packs) draws the missing model instead of crashing the Create menu (#2).
- `LazyFluidRenderHandler` (in the bundled registrate_fabric jar): Create fluids fill their sprites from the block atlas on demand, so mods that read them early (Moonlight/Supplementaries soft fluids) no longer get nulls and fail to load the world (#3). `FluidBuilder.class` is the published class with its one `SimpleFluidRenderHandler` reference switched to this class in the constant pool.

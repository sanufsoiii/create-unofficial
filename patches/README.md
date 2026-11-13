# 6.0.10.1 hotfix sources

The 6.0.10.1 release is the published 6.0.10 jar with these classes recompiled (decompiled from that jar,
intermediary names) because the current source tree differs from what was published:

- `KnockbackPacket` + `KnockbackPacketClient`: client-only code moved out so dedicated servers can load `AllPackets`.
- `CommonEvents`: the client-only `ChainConveyorConnectionHandler` is only registered on the client.
- `GuiGameElement`: GUI block previews (JEI sequenced assembly etc.) render at full brightness instead of black.

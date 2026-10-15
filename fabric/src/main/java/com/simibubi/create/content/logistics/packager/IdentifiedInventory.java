package com.simibubi.create.content.logistics.packager;

import org.jetbrains.annotations.Nullable;

/**
 * An item inventory, possibly with an associated InventoryIdentifier.
 */
public record IdentifiedInventory(@Nullable Object identifier, Object handler) {
}

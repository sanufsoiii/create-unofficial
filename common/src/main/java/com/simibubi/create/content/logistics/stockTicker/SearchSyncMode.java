package com.simibubi.create.content.logistics.stockTicker;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;

public enum SearchSyncMode implements StringRepresentable {
	// Typing anything in JEI or the StockKeeper UI will sync it to both
	SYNC_BOTH(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_BOTH),
	// Typing in JEI will sync it to the stockkeeper, but not vice versa
	SYNC_FROM_JEI(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_JEI),
	// Typing in the stockkeeper will sync it to JEI, but not vice versa
	SYNC_FROM_STOCK_KEEPER(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_STOCK_KEEPER),
	// Do nothing, both search boxes work separately
	NONE(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_DISABLED);

	public final AllGuiTextures buttonTexture;

	SearchSyncMode(AllGuiTextures buttonTexture) {
		this.buttonTexture = buttonTexture;
	}

	public boolean isBothOr(SearchSyncMode mode) {
		return this == SearchSyncMode.SYNC_BOTH || this == mode;
	}

	public SearchSyncMode next() {
		SearchSyncMode[] vals = values();
		return vals[(this.ordinal() + 1) % vals.length];
	}

	@Override
	public String getSerializedName() {
		return Lang.asId(name());
	}
}

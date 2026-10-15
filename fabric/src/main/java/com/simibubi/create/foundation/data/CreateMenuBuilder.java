package com.simibubi.create.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.fabric.EnvExecutor;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import dev.architectury.registry.menu.MenuRegistry;
import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class CreateMenuBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>, P>
		extends AbstractBuilder<MenuType<?>, MenuType<T>, P, CreateMenuBuilder<T, S, P>> {

	@FunctionalInterface
	public interface MenuFactory<T extends AbstractContainerMenu> {
		T create(MenuType<T> type, int windowId, Inventory inv, RegistryFriendlyByteBuf data);
	}

	private final MenuFactory<T> factory;
	private final NonNullSupplier<ScreenFactory<T, S>> screenFactory;

	public CreateMenuBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback,
			MenuFactory<T> factory, NonNullSupplier<ScreenFactory<T, S>> screenFactory) {
		super(owner, parent, name, callback, Registries.MENU);
		this.factory = factory;
		this.screenFactory = screenFactory;
	}

	@Override
	protected @NonnullType MenuType<T> createEntry() {
		NonNullSupplier<MenuType<T>> supplier = this.asSupplier();
		MenuType<T> ret = MenuRegistry.ofExtended((windowId, inv, data) -> {
			RegistryFriendlyByteBuf buf = data instanceof RegistryFriendlyByteBuf rfBuf
				? rfBuf
				: new RegistryFriendlyByteBuf(data, inv.player.registryAccess());
			return factory.create(supplier.get(), windowId, inv, buf);
		});
		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			ScreenFactory<T, S> sf = this.screenFactory.get();
			MenuScreens.register(ret, sf::create);
		});
		return ret;
	}

	@Override
	protected RegistryEntry<MenuType<?>, MenuType<T>> createEntryWrapper(DeferredHolder<MenuType<?>, MenuType<T>> delegate) {
		return new MenuEntry<>(getOwner(), delegate);
	}

	@Override
	public MenuEntry<T> register() {
		return (MenuEntry<T>) super.register();
	}
}

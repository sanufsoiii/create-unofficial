package com.simibubi.create;

import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.createmod.catnip.client.ConflictSafeKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public enum AllKeys {

	TOOL_MENU("toolmenu", GLFW.GLFW_KEY_LEFT_ALT, "Focus Schematic Overlay"),
	ACTIVATE_TOOL(GLFW.GLFW_KEY_LEFT_CONTROL),
	TOOLBELT("toolbelt", GLFW.GLFW_KEY_LEFT_ALT, "Access Nearby Toolboxes"),
	ROTATE_MENU("rotate_menu", GLFW.GLFW_KEY_UNKNOWN, "Open Block Rotation Menu"),

	SHIFT_MODIFIER("shift_modifier", GLFW.GLFW_KEY_LEFT_SHIFT, "Shift Modifier", true),
	CTRL_MODIFIER("ctrl_modifier", GLFW.GLFW_KEY_LEFT_CONTROL, "Ctrl Modifier", true),
	ALT_MODIFIER("alt_modifier", GLFW.GLFW_KEY_LEFT_ALT, "Alt Modifier", true),
	;

	private KeyMapping keybind;
	private final String description;
	private final String translation;
	private final int key;
	private final boolean modifiable;
	private final boolean conflictSafe;

	AllKeys(int defaultKey) {
		this("", defaultKey, "");
	}

	AllKeys(String description, int defaultKey, String translation) {
		this(description, defaultKey, translation, false);
	}

	AllKeys(String description, int defaultKey, String translation, boolean conflictSafe) {
		this.description = Create.ID + ".keyinfo." + description;
		this.key = defaultKey;
		this.modifiable = !description.isEmpty();
		this.translation = translation;
		this.conflictSafe = conflictSafe;
	}

	public static void provideLang(BiConsumer<String, String> consumer) {
		for (AllKeys key : values())
			if (key.modifiable)
				consumer.accept(key.description, key.translation);
	}

	public static void register() {
		for (AllKeys key : values()) {
			key.getKeybind();
			if (!key.modifiable)
				continue;

			KeyMappingRegistry.register(key.keybind);
		}
	}

	public KeyMapping getKeybind() {
		if (keybind == null) {
			if (conflictSafe) {
				keybind = new ConflictSafeKeyMapping(description, key, Create.NAME);
			} else {
				keybind = new KeyMapping(description, key, Create.NAME);
			}
		}
		return keybind;
	}

	public boolean isPressed() {
		if (!modifiable)
			return isKeyDown(key);
		return getKeybind().isDown();
	}

	public String getBoundKey() {
		return getKeybind().getTranslatedKeyMessage()
			.getString()
			.toUpperCase();
	}

	public boolean doesModifierAndCodeMatch(int code) {
		return doesModifierAndCodeMatch(getKeybind(), code);
	}

	@ExpectPlatform
	public static boolean doesModifierAndCodeMatch(KeyMapping keyMapping, int code) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static InputConstants.Key getBoundKey(KeyMapping keyMapping) {
		throw new AssertionError();
	}

	public static boolean isKeyDown(int key) {
		return InputConstants.isKeyDown(Minecraft.getInstance()
			.getWindow()
			.getWindow(), key);
	}

	public static boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(Minecraft.getInstance()
			.getWindow()
			.getWindow(), button) == 1;
	}

	public static boolean ctrlDown() {
		return isKeyDown(getBoundKey(CTRL_MODIFIER.getKeybind()).getValue());
	}

	public static boolean shiftDown() {
		return isKeyDown(getBoundKey(SHIFT_MODIFIER.getKeybind()).getValue());
	}

	public static boolean altDown() {
		return isKeyDown(getBoundKey(ALT_MODIFIER.getKeybind()).getValue());
	}

}

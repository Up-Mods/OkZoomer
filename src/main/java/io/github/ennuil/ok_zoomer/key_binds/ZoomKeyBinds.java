package io.github.ennuil.ok_zoomer.key_binds;

import com.mojang.blaze3d.platform.InputUtil;

import org.lwjgl.glfw.GLFW;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.option.KeyBind;

// Manages the zoom keybinds themselves
public class ZoomKeyBinds {
	// The "Zoom" category
	public static final String ZOOM_CATEGORY = "key.ok_zoomer.category";

	// The zoom key bind, which will be registered
	public static final KeyBind ZOOM_KEY = new KeyBind("key.ok_zoomer.zoom", GLFW.GLFW_KEY_C, ZOOM_CATEGORY);

	// The "Decrease Zoom" key bind
	public static final KeyBind DECREASE_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.decrease_zoom");

	// The "Increase Zoom" key bind
	public static final KeyBind INCREASE_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.increase_zoom");

	// The "Reset Zoom" key bind
	public static final KeyBind RESET_ZOOM_KEY = getExtraKeyBind("key.ok_zoomer.reset_zoom");

	// The method used to check if the zoom manipulation key binds should be disabled, can be used by other mods.
	public static final boolean areExtraKeyBindsEnabled() {
		if (!OkZoomerConfigManager.isConfigLoaded.isPresent()) {
			OkZoomerConfigManager.loadModConfig();
		}

		return OkZoomerConfigManager.configInstance.features().getExtraKeyBinds();
	}

	// The method used to get the extra keybinds, if disabled, return null.
	public static final KeyBind getExtraKeyBind(String translationKey) {
		if (areExtraKeyBindsEnabled()) {
			return new KeyBind(translationKey, InputUtil.UNKNOWN_KEY.getKeyCode(), ZOOM_CATEGORY);
		}

		return null;
	}
}

package io.github.ennuil.okzoomer.key_binds;

import com.mojang.blaze3d.platform.InputUtil;

import org.lwjgl.glfw.GLFW;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;

// Manages the zoom keybinds themselves
public class ZoomKeyBinds {
    // The zoom key bind, which will be registered
    public static final KeyBind ZOOM_KEY = KeyBindingHelper.registerKeyBinding(
        new KeyBind("key.okzoomer.zoom", GLFW.GLFW_KEY_C, "key.okzoomer.category"));
    
    // The "Decrease Zoom" key bind
    public static final KeyBind DECREASE_ZOOM_KEY = getExtraKeybind("key.okzoomer.decrease_zoom");

    // The "Increase Zoom" key bind
    public static final KeyBind INCREASE_ZOOM_KEY = getExtraKeybind("key.okzoomer.increase_zoom");

    // The "Reset Zoom" key bind
    public static final KeyBind RESET_ZOOM_KEY = getExtraKeybind("key.okzoomer.reset_zoom");

    // The method used to check if the zoom manipulation key binds should be disabled, can be used by other mods.
    public static final boolean areExtraKeyBindsEnabled() {
        if (!OkZoomerConfigManager.isConfigLoaded.isPresent()) {
            OkZoomerConfigManager.loadModConfig();
        }
        return OkZoomerConfigManager.configInstance.features().getExtraKeyBinds();
    }
    
    // The method used to get the extra keybinds, if disabled, return null.
    public static final KeyBind getExtraKeybind(String translationKey) {
        if (areExtraKeyBindsEnabled()) {
            return KeyBindingHelper.registerKeyBinding(
                new KeyBind(translationKey, InputUtil.UNKNOWN_KEY.getKeyCode(), "key.okzoomer.category"));
        }
        return null;
    }
}
package io.github.ennuil.okzoomer.keybinds;

import com.mojang.blaze3d.platform.InputUtil;

import org.lwjgl.glfw.GLFW;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;

// Manages the zoom keybinds themselves.
public class ZoomKeyBinds {
    // The zoom keybinding, which will be registered.
    public static final KeyBind ZOOM_KEY = KeyBindingHelper.registerKeyBinding(
        new KeyBind("key.okzoomer.zoom", GLFW.GLFW_KEY_C, "key.okzoomer.category"));
    
    // The "Decrease Zoom" keybinding.
    public static final KeyBind DECREASE_ZOOM_KEY = getExtraKeybind("key.okzoomer.decrease_zoom");

    // The "Increase Zoom" keybinding.
    public static final KeyBind INCREASE_ZOOM_KEY = getExtraKeybind("key.okzoomer.increase_zoom");

    // The "Reset Zoom" keybinding.
    public static final KeyBind RESET_ZOOM_KEY = getExtraKeybind("key.okzoomer.reset_zoom");

    // The boolean used to keep track of the "Extra Keybinds" option's initial value.
    private static boolean extraKeybinds = false;

    // The method used to check if the zoom manipulation keybinds should be disabled, can be used by other mods.
    public static final boolean areExtraKeybindsEnabled() {
        if (!OkZoomerConfigManager.isConfigLoaded.isPresent()) {
            OkZoomerConfigManager.loadModConfig();
        }
        extraKeybinds = OkZoomerConfigManager.INSTANCE.features().extraKeybinds();
        return extraKeybinds;
    }
    
    // The method used to get the extra keybinds, if disabled, return null.
    public static final KeyBind getExtraKeybind(String translationKey) {
        if (areExtraKeybindsEnabled()) {
            return KeyBindingHelper.registerKeyBinding(
                new KeyBind(translationKey, InputUtil.UNKNOWN_KEY.getKeyCode(), "key.okzoomer.category"));
        }
        return null;
    }
}
package io.github.ennuil.okzoomer.keybinds;

import org.lwjgl.glfw.GLFW;

import io.github.ennuil.okzoomer.config.OkZoomerConfig;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

// Manages the zoom keybinds themselves.
public class ZoomKeybinds {
    // The zoom keybinding, which will be registered.
    public static final KeyBinding zoomKey = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("key.okzoomer.zoom", GLFW.GLFW_KEY_C, "key.okzoomer.category"));
    
    // The "Decrease Zoom" keybinding.
    public static final KeyBinding decreaseZoomKey = getExtraKeybind("key.okzoomer.decrease_zoom");

    // The "Increase Zoom" keybinding.
    public static final KeyBinding increaseZoomKey = getExtraKeybind("key.okzoomer.increase_zoom");

    // The "Reset Zoom" keybinding.
    public static final KeyBinding resetZoomKey = getExtraKeybind("key.okzoomer.reset_zoom");

    // The boolean used to keep track of the "Extra Keybinds" option's initial value.
    private static boolean extraKeybinds = false;

    // The method used to check if the zoom manipulation keybinds should be disabled, can be used by other mods.
    public static final boolean areExtraKeybindsEnabled() {
        if (!OkZoomerConfig.isConfigLoaded) {
            OkZoomerConfig.loadModConfig();
        }
        extraKeybinds = OkZoomerConfigPojo.features.extraKeybinds;
        return extraKeybinds;
    }
    
    // The method used to get the extra keybinds, if disabled, return null.
    public static final KeyBinding getExtraKeybind(String translationKey) {
        if (areExtraKeybindsEnabled()) {
            return KeyBindingHelper.registerKeyBinding(
                new KeyBinding(translationKey, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));
        }
        return null;
    }
}
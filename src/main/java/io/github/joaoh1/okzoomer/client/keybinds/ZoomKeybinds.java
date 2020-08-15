package io.github.joaoh1.okzoomer.client.keybinds;

import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

//Manages the zoom keybinds themselves.
public class ZoomKeybinds {
    //The zoom keybinding, which will be registered.
	public static final KeyBinding zoomKey = KeyBindingHelper.registerKeyBinding(
		new KeyBinding("key.okzoomer.zoom", InputUtil.Type.KEYSYM, getDefaultZoomKey(), "key.okzoomer.category"));
	
	//The "Decrease Zoom" keybinding.
	public static final KeyBinding decreaseZoomKey = getExtraKeybind("key.okzoomer.decrease_zoom");

	//The "Increase Zoom" keybinding.
	public static final KeyBinding increaseZoomKey = getExtraKeybind("key.okzoomer.increase_zoom");

	//The "Reset Zoom" keybinding.
	public static final KeyBinding resetZoomKey = getExtraKeybind("key.okzoomer.reset_zoom");
	
	//The method used for getting the default zoom key, which can be either C or Z.
	public static final int getDefaultZoomKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			ZoomUtils.modLogger.info("[Ok Zoomer] OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
	}

	//The boolean used to keep track of the "Extra Keybinds" option's initial value.
	private static boolean extraKeybinds = false;

	//The method used to check if the zoom manipulation keybinds should be disabled, can be used by other mods.
	public static final boolean areExtraKeybindsEnabled() {
		if (!OkZoomerConfig.isConfigLoaded) {
			OkZoomerConfig.loadModConfig();
		}
		extraKeybinds = OkZoomerConfigPojo.features.extraKeybinds;
		return extraKeybinds;
	}
    
    //The method used to get the extra keybinds, if disabled, return null.
	public static final KeyBinding getExtraKeybind(String translationKey) {
		if (areExtraKeybindsEnabled()) {
			return KeyBindingHelper.registerKeyBinding(
				new KeyBinding(translationKey, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));
		}
		return null;
	}
}
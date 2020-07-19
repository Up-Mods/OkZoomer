package io.github.joaoh1.okzoomer.client.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.client.OkZoomerClientMod;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ZoomUtils {
    //The logger, used here for letting the user know that the zoom key isn't C if Z is chosen.
    public static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");
	
	//The method used for getting the default zoom key, which can be either C or Z.
	public static final int getDefaultZoomKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			modLogger.info("[Ok Zoomer Next] OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
	}

	//The boolean used to keep track of the state of extra keybinds.
	private static boolean extraKeybinds = false;

	//The method used to check if the zoom manipulation keybinds should be disabled, can be used by other mods.
	public static final boolean areExtraKeybindsEnabled() {
		if (!OkZoomerConfig.isConfigLoaded) {
			OkZoomerConfig.loadModConfig();
			extraKeybinds = OkZoomerConfigPojo.features.extraKeybinds;
		}
		return extraKeybinds;
	}
	
	//The method used to get zoom manipulation keybinds, if disabled, return null.
	public static final KeyBinding getZoomManipulationKeybind(String translationKey) {
		if (areExtraKeybindsEnabled()) {
			return KeyBindingHelper.registerKeyBinding(
				new KeyBinding(translationKey, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.okzoomer.category"));
		}
		return null;
	}
    
    //The zoom signal, which is managed in an event and used by other mixins.
	public static boolean zoomState = false;

	//Used for post-zoom actions like updating the terrain.
	public static boolean lastZoomState = false;

	//The zoom divisor, managed by the zoom press and zoom scrolling. Used by other mixins.
	public static double zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;

	//The zoom FOV multipliers. Used by the GameRenderer mixin.
	public static float zoomFovMultiplier = 1.0F;
	public static float lastZoomFovMultiplier = 1.0F;

	//Used in order to allow the server to disable the client's zoom.
	public static boolean disableZoom = false;

	//Used in order to allow the server to disable the client's zoom scrolling.
	public static boolean disableZoomScrolling = false;

	//Used in order to allow the server to force the zoom to behave like OptiFine's.
	public static boolean optifineMode = false;

    //The method used for changing the zoom divisor, used by zoom scrolling and the keybinds.
	public static final void changeZoomDivisor(boolean increase) {
		//If the zoom is disabled, don't allow for zoom scrolling
		if (disableZoom || disableZoomScrolling) {
			return;
		}

		double changedZoomDivisor;
		double lesserChangedZoomDivisor;

		if (increase) {
			changedZoomDivisor = zoomDivisor + OkZoomerConfigPojo.values.scrollStep;
			lesserChangedZoomDivisor = zoomDivisor + OkZoomerConfigPojo.values.lesserScrollStep;
		} else {
			changedZoomDivisor = zoomDivisor - OkZoomerConfigPojo.values.scrollStep;
			lesserChangedZoomDivisor = zoomDivisor - OkZoomerConfigPojo.values.lesserScrollStep;
			lastZoomState = true;
		}

		if (lesserChangedZoomDivisor <= OkZoomerConfigPojo.values.zoomDivisor) {
			changedZoomDivisor = lesserChangedZoomDivisor;
		}

		if (changedZoomDivisor >= OkZoomerConfigPojo.values.minimumZoomDivisor) {
			if (changedZoomDivisor <= OkZoomerConfigPojo.values.maximumZoomDivisor) {
				zoomDivisor = changedZoomDivisor;
				System.out.println(zoomDivisor);
			}
		}
	}

	//The equivalent of GameRenderer's updateFovMultiplier but for zooming. Used by zoom transitions.
	public static final void updateZoomFovMultiplier() {
		float zoomMultiplier = 1.0F;

		if (ZoomUtils.zoomState) {
			zoomMultiplier /= ZoomUtils.zoomDivisor;
		}

		lastZoomFovMultiplier = zoomFovMultiplier;
		
		if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.SMOOTH)) {
			zoomFovMultiplier += (zoomMultiplier - zoomFovMultiplier) * OkZoomerConfigPojo.values.smoothMultiplier;
		} else if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.SINE)) {
			zoomFovMultiplier += Math.sin(zoomMultiplier - zoomFovMultiplier);
		}
	}

	//This method is used in order to hijack the "Save Toolbar Activator" keybind's key, which is C.
	public static final void hijackSaveToolbarActivatorKey(MinecraftClient client) {
		if (OkZoomerConfigPojo.technical.hijackSaveToolbarActivatorKey) {
			if (OkZoomerClientMod.zoomKeyBinding.isDefault() && ZoomUtils.getDefaultZoomKey() == GLFW.GLFW_KEY_C) {
				if (client.options.keySaveToolbarActivator.isDefault()) {
					modLogger.info("[Ok Zoomer Next] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated.");
					client.options.keySaveToolbarActivator.setBoundKey(InputUtil.UNKNOWN_KEY);
					client.options.write();
					KeyBinding.updateKeysByCode();
				}
			}
			OkZoomerConfigPojo.technical.hijackSaveToolbarActivatorKey = false;
			OkZoomerConfig.saveModConfig();
		}
	}
}
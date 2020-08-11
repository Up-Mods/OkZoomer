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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ZoomUtils {
    //The logger, used everywhere to print messages to the console.
	public static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer");
	
	//The IDs for packets that allows the server to have some control on the zoom.
	public static final Identifier DISABLE_ZOOM_PACKET_ID = new Identifier("okzoomer", "disable_zoom");
	public static final Identifier DISABLE_ZOOM_SCROLLING_PACKET_ID = new Identifier("okzoomer", "disable_zoom_scrolling");
	
	//The method used for getting the default zoom key, which can be either C or Z.
	public static final int getDefaultZoomKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			modLogger.info("[Ok Zoomer] OptiFabric was detected! Using Z as the default key.");
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
	public static final KeyBinding getExtraKeybind(String translationKey) {
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
			}
		}
	}

	//The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak.
	public static final void resetZoomDivisor() {
		zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
		lastZoomState = true;
	}

	//The method used for unbinding the "Save Toolbar Activator"
	public static final void unbindConflictingKey(MinecraftClient client, boolean userPrompted) {
		if (OkZoomerClientMod.zoomKeyBinding.isDefault() && ZoomUtils.getDefaultZoomKey() == GLFW.GLFW_KEY_C) {
			if (client.options.keySaveToolbarActivator.isDefault()) {
				if (userPrompted) {
					ZoomUtils.modLogger.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding...");
				} else {
					ZoomUtils.modLogger.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated until specified in the config.");
				}
				client.options.keySaveToolbarActivator.setBoundKey(InputUtil.UNKNOWN_KEY);
				client.options.write();
				KeyBinding.updateKeysByCode();
			} else {
				ZoomUtils.modLogger.info("[Ok Zoomer] No conflicts with the \"Save Toolbar Activator\" keybind was found!");
			}
		}
	}

	//The equivalent of GameRenderer's updateFovMultiplier but for zooming. Used by zoom transitions.
	public static final void updateZoomFovMultiplier() {
		float zoomMultiplier = 1.0F;
		double dividedZoomMultiplier = 1.0 / ZoomUtils.zoomDivisor;

		if (ZoomUtils.zoomState) {
			zoomMultiplier = (float)dividedZoomMultiplier;
		}

		lastZoomFovMultiplier = zoomFovMultiplier;
		
		if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.SMOOTH)) {
			zoomFovMultiplier += (zoomMultiplier - zoomFovMultiplier) * OkZoomerConfigPojo.values.smoothMultiplier;
		} else if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.LINEAR)) {
			double linearStep = dividedZoomMultiplier;
			if (linearStep < OkZoomerConfigPojo.values.minimumLinearStep) {
				linearStep = OkZoomerConfigPojo.values.minimumLinearStep;
			}
			if (linearStep > OkZoomerConfigPojo.values.maximumLinearStep) {
				linearStep = OkZoomerConfigPojo.values.maximumLinearStep;
			}
			zoomFovMultiplier = MathHelper.stepTowards(zoomFovMultiplier, zoomMultiplier, (float)linearStep);
		}
	}
}
package io.github.joaoh1.okzoomer.client.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.client.keybinds.ZoomKeybinds;
import io.github.joaoh1.okzoomer.client.packets.ZoomPackets;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

public class ZoomUtils {
    //The logger, used everywhere to print messages to the console.
	public static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer");
    
    //The zoom signal, which is managed in an event and used by other mixins.
	public static boolean zoomState = false;

	//Used for post-zoom actions like updating the terrain.
	public static boolean lastZoomState = false;

	//The zoom divisor, managed by the zoom press and zoom scrolling. Used by other mixins.
	public static double zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;

	//The zoom FOV multipliers. Used by the GameRenderer mixin.
	public static float zoomFovMultiplier = 1.0F;
	public static float lastZoomFovMultiplier = 1.0F;

    //The method used for changing the zoom divisor, used by zoom scrolling and the keybinds.
	public static final void changeZoomDivisor(boolean increase) {
		//If the zoom is disabled, don't allow for zoom scrolling
		if (ZoomPackets.disableZoom || ZoomPackets.disableZoomScrolling) {
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
		if (ZoomPackets.disableZoom || ZoomPackets.disableZoomScrolling) {
			return;
		}

		zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
		lastZoomState = true;
	}

	//The method used for unbinding the "Save Toolbar Activator"
	public static final void unbindConflictingKey(MinecraftClient client, boolean userPrompted) {
		if (ZoomKeybinds.zoomKey.isDefault() && ZoomKeybinds.getDefaultZoomKey() == GLFW.GLFW_KEY_C) {
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
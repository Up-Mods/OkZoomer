package io.github.joaoh1.okzoomer.client.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import net.fabricmc.loader.api.FabricLoader;

public class ZoomUtils {
    //The logger, used here for letting the user know that the zoom key isn't C if Z is chosen.
    protected static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer Next");
	
	public static final int getDefaultZoomKey() {
		//If OptiFabric (and therefore, OptiFine) is detected, use Z as the default value instead.
		if (FabricLoader.getInstance().isModLoaded("optifabric")) {
			modLogger.info("[Ok Zoomer Next] OptiFabric was detected! Using Z as the default key.");
			return GLFW.GLFW_KEY_Z;
		} else {
			return GLFW.GLFW_KEY_C;
		}
    }
    
    //The zoom signal, which is managed in an event and used by other mixins.
	public static boolean isZoomKeyPressed = false;

	//Used for post-zoom actions like updating the terrain.
	public static boolean zoomHasHappened = false;

	//The zoom divisor, managed by the zoom press and zoom scrolling. Used by other mixins.
	public static double zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;

	//Used in order to allow the server to disable the client's zoom.
	public static boolean isZoomDisabled = false;

    //The method used for changing the zoom divisor, used by zoom scrolling and the keybinds.
	public static void changeZoomDivisor(boolean increase) {
		//If the zoom is disabled, don't allow for zoom scrolling
		if (isZoomDisabled) {
			return;
		}

		if (increase) {
			if (zoomDivisor < OkZoomerConfigPojo.values.maximumZoomDivisor) {
				zoomDivisor += 0.5D;
			} else {
				zoomDivisor = OkZoomerConfigPojo.values.maximumZoomDivisor;
			}
		} else {
			if (zoomDivisor > OkZoomerConfigPojo.values.minimumZoomDivisor) {
				zoomDivisor -= 0.5D;
				zoomHasHappened = true;
			} else {
				zoomDivisor = OkZoomerConfigPojo.values.minimumZoomDivisor;
			}
		}
	}
}
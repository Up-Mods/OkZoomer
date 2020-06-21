package io.github.joaoh1.okzoomer.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import me.shedaniel.fiber2cloth.api.ClothSetting;

public class OkZoomerConfigPojo {
	@ClothSetting.Tooltip()
	@Setting.Constrain.Range(min = Double.MIN_NORMAL)
	@Setting(comment = "The divisor applied to the FOV when zooming.")
	public static double zoomDivisor = 4.0;

	@ClothSetting.Tooltip()
	@Setting(comment = "Enables the cinematic camera while zooming.\n\"off\" disables it.\n\"vanilla\" mimics Vanilla's Cinematic Camera.\n\"multiplied\" is a multiplied variant of \"vanilla\".")
	public static CinematicCameraOptions cinematicCamera = CinematicCameraOptions.OFF;

	public enum CinematicCameraOptions {
		OFF,
		VANILLA,
		MULTIPLIED
	}

	@Setting.Constrain.Range(min = Double.MIN_NORMAL)
	@Setting(comment = "The multiplier used on the multiplied cinematic camera.")
	public static double cinematicMultiplier = 4.0;

	@ClothSetting.Tooltip()
	@Setting(comment = "Reduces the mouse sensitivity when zooming.")
	public static boolean reduceSensitivity = true;

	@ClothSetting.Tooltip()
	@ClothSetting.EnumHandler(ClothSetting.EnumHandler.EnumDisplayOption.BUTTON)
	@Setting(comment = "Adds transitions between zooms.\n\"off\" disables it.\n\"smooth\" starts fast and ends slow.")
	public static ZoomTransitionOptions zoomTransition = ZoomTransitionOptions.SMOOTH;

	public enum ZoomTransitionOptions {
		OFF,
		SMOOTH
	}

	@ClothSetting.Tooltip()
	@Setting(comment = "The behavior of the zoom key.\n\"hold\" needs the zoom key to be hold.\n\"toggle\" has the zoom key toggle the zoom.\n\"persistent\" always zooms, with the zoom key only being used for zoom scrolling.")
	public static ZoomModes zoomMode = ZoomModes.HOLD;

	public enum ZoomModes {
		HOLD,
		TOGGLE,
		PERSISTENT
	}

	@ClothSetting.Tooltip("config.okzoomer.zoom_divisor.tooltip")
	@Setting(comment = "Allows to increase or decrease zoom by scrolling.")
	public static boolean zoomScrolling = true;

	@ClothSetting.Tooltip()
	@Setting.Constrain.Range(min = Double.MIN_NORMAL)
	@Setting(comment = "The minimum value that you can scroll down.")
	public static double minimumZoomDivisor = 1.0;

	@ClothSetting.Tooltip()
	@Setting.Constrain.Range(min = Double.MIN_NORMAL)
	@Setting(comment = "The maximum value that you can scroll up.")
	public static double maximumZoomDivisor = 50.0;
}
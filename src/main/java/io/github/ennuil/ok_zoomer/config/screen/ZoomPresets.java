package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import org.quiltmc.config.api.values.TrackedValue;

import java.util.Map;

public class ZoomPresets {
	public static final Map<TrackedValue<?>, Object> CAMERA = Map.of();

	public static final Map<TrackedValue<?>, Object> COMPETITIVE = Map.of(
		OkZoomerConfigManager.CONFIG.features.reduceViewBobbing, false,
		OkZoomerConfigManager.CONFIG.features.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair, false
	);

	public static final Map<TrackedValue<?>, Object> CLASSIC = Map.of(
		OkZoomerConfigManager.CONFIG.features.cinematicCamera, ConfigEnums.CinematicCameraOptions.VANILLA,
		OkZoomerConfigManager.CONFIG.features.reduceSensitivity, false,
		OkZoomerConfigManager.CONFIG.features.zoomTransition, ConfigEnums.ZoomTransitionOptions.OFF,
		OkZoomerConfigManager.CONFIG.features.reduceViewBobbing, false,
		OkZoomerConfigManager.CONFIG.features.zoomScrolling, false,
		OkZoomerConfigManager.CONFIG.features.persistentInterface, true,
		//OkZoomerConfigManager.CONFIG.features.extraKeyBinds, false - We'll maybe do that once we guarantee that is dynamic
		OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair, false
	);

	public static final Map<TrackedValue<?>, Object> PERSISTENT = Map.of(
		OkZoomerConfigManager.CONFIG.features.zoomMode, ConfigEnums.ZoomModes.PERSISTENT,
		OkZoomerConfigManager.CONFIG.features.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor, 1.0,
		OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps, 0,
		OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps, 38,
		OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair, false
	);

	public static final Map<TrackedValue<?>, Object> SPYGLASS = Map.ofEntries(
		Map.entry(OkZoomerConfigManager.CONFIG.features.reduceViewBobbing, false),
		Map.entry(OkZoomerConfigManager.CONFIG.features.zoomScrolling, false),
		Map.entry(OkZoomerConfigManager.CONFIG.features.persistentInterface, true),
		Map.entry(OkZoomerConfigManager.CONFIG.features.zoomOverlay, ConfigEnums.ZoomOverlays.SPYGLASS),
		Map.entry(OkZoomerConfigManager.CONFIG.features.spyglassMode, ConfigEnums.SpyglassMode.BOTH),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor, 10.0),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps, 8),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps, 16),
		Map.entry(OkZoomerConfigManager.CONFIG.transitionValues.smoothTransitionFactor, 0.5),
		Map.entry(OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair, false),
		Map.entry(OkZoomerConfigManager.CONFIG.tweaks.useSpyglassSounds, true)
	);

	public static final Map<ConfigEnums.ConfigEnum, Map<TrackedValue<?>, Object>> PRESET_ENUM_TO_PRESET = Map.of(
		ConfigEnums.ZoomPresets.CAMERA, CAMERA,
		ConfigEnums.ZoomPresets.COMPETITIVE, COMPETITIVE,
		ConfigEnums.ZoomPresets.CLASSIC, CLASSIC,
		ConfigEnums.ZoomPresets.PERSISTENT, PERSISTENT,
		ConfigEnums.ZoomPresets.SPYGLASS, SPYGLASS
	);
}

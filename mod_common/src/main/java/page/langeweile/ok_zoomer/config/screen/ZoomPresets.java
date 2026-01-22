package page.langeweile.ok_zoomer.config.screen;

import org.quiltmc.config.api.values.TrackedValue;
import page.langeweile.ok_zoomer.config.ConfigEnums;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;

import java.util.Map;

public class ZoomPresets {
	public static final Map<TrackedValue<?>, Object> CAMERA = Map.of();

	public static final Map<TrackedValue<?>, Object> COMPETITIVE = Map.of(
		OkZoomerConfigManager.CONFIG.appearance.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.appearance.hideCrosshair, false,
		OkZoomerConfigManager.CONFIG.appearance.reduceViewBobbing, false
	);

	public static final Map<TrackedValue<?>, Object> CLASSIC = Map.of(
		OkZoomerConfigManager.CONFIG.zoomTransition.startTransition, ConfigEnums.ZoomTransitionModes.INSTANT,
		OkZoomerConfigManager.CONFIG.zoomTransition.endTransition, ConfigEnums.ZoomTransitionModes.INSTANT,
		OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionTicks, 0,
		OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionTicks, 0,
		OkZoomerConfigManager.CONFIG.appearance.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.appearance.hideCrosshair, false,
		OkZoomerConfigManager.CONFIG.appearance.reduceViewBobbing, false,
		OkZoomerConfigManager.CONFIG.controls.cinematicCamera, true,
		OkZoomerConfigManager.CONFIG.controls.reduceSensitivity, false,
		OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling, false
		//OkZoomerConfigManager.CONFIG.controls.extraKeyBinds, false - We'll maybe do that once we guarantee that is dynamic
	);

	public static final Map<TrackedValue<?>, Object> ZOOMINATOR = Map.of(
		OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionTicks, 20,
		OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionTicks, 10,
		OkZoomerConfigManager.CONFIG.appearance.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.appearance.hideCrosshair, false,
		OkZoomerConfigManager.CONFIG.zoomScrolling.resetZoomWithMouse, false,
		// Best effort attempt at replicating Zoomify's scroll feeling without its jankiness
		OkZoomerConfigManager.CONFIG.zoomScrolling.transitionTicks, 12,
		OkZoomerConfigManager.CONFIG.zoomScrolling.scrollResolution, 3,
		OkZoomerConfigManager.CONFIG.zoomScrolling.defaultScrollStep, 6,
		OkZoomerConfigManager.CONFIG.zoomScrolling.scrollStepLimit, 24
	);

	public static final Map<TrackedValue<?>, Object> PERSISTENT = Map.of(
		OkZoomerConfigManager.CONFIG.controls.zoomMode, ConfigEnums.ZoomModes.PERSISTENT,
		OkZoomerConfigManager.CONFIG.appearance.persistentInterface, true,
		OkZoomerConfigManager.CONFIG.appearance.hideCrosshair, false,
		OkZoomerConfigManager.CONFIG.zoomScrolling.defaultScrollStep, 0
	);

	public static final Map<TrackedValue<?>, Object> SPYGLASS = Map.ofEntries(
		Map.entry(OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionTicks, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionTicks, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling, false),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.transitionTicks, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.scrollBase, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.scrollResolution, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.defaultScrollStep, 10),
		Map.entry(OkZoomerConfigManager.CONFIG.zoomScrolling.scrollStepLimit, 20),
		Map.entry(OkZoomerConfigManager.CONFIG.appearance.persistentInterface, true),
		Map.entry(OkZoomerConfigManager.CONFIG.appearance.hideCrosshair, false),
		Map.entry(OkZoomerConfigManager.CONFIG.appearance.reduceViewBobbing, false),
		Map.entry(OkZoomerConfigManager.CONFIG.appearance.zoomOverlay, ConfigEnums.ZoomOverlays.SPYGLASS),
		Map.entry(OkZoomerConfigManager.CONFIG.controls.spyglassSounds, true),
		Map.entry(OkZoomerConfigManager.CONFIG.controls.spyglassMode, ConfigEnums.SpyglassModes.BOTH)
	);

	public static final Map<ConfigEnums.ConfigEnum, Map<TrackedValue<?>, Object>> PRESET_ENUM_TO_PRESET = Map.of(
		ConfigEnums.ZoomPresets.CAMERA, CAMERA,
		ConfigEnums.ZoomPresets.COMPETITIVE, COMPETITIVE,
		ConfigEnums.ZoomPresets.CLASSIC, CLASSIC,
		ConfigEnums.ZoomPresets.ZOOMINATOR, ZOOMINATOR,
		ConfigEnums.ZoomPresets.PERSISTENT, PERSISTENT,
		ConfigEnums.ZoomPresets.SPYGLASS, SPYGLASS
	);
}

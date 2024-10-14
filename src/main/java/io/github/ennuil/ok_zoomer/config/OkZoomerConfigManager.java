package io.github.ennuil.ok_zoomer.config;

import io.github.ennuil.ok_zoomer.zoom.Zoom;
import io.github.ennuil.ok_zoomer.zoom.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.modifiers.ContainingMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.overlays.SpyglassZoomOverlay;
import io.github.ennuil.ok_zoomer.zoom.transitions.InstantTransitionMode;
import io.github.ennuil.ok_zoomer.zoom.transitions.SmoothTransitionMode;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.utils.ModUtils;
import io.github.ennuil.ok_zoomer.wrench_wrapper.WrenchWrapper;
import io.github.ennuil.ok_zoomer.zoom.transitions.LinearTransitionMode;
import io.github.ennuil.ok_zoomer.zoom.modifiers.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.overlays.ZoomerZoomOverlay;
import net.minecraft.resources.ResourceLocation;

public class OkZoomerConfigManager {
	public static final OkZoomerConfig CONFIG = WrenchWrapper.create(ModUtils.MOD_NAMESPACE, "config", OkZoomerConfig.class);

	public static void init() {
		// On initialization, configure our zoom instance
		OkZoomerConfigManager.configureZoomInstance();

		CONFIG.registerCallback(config -> OkZoomerConfigManager.configureZoomInstance());
	}

	public static void configureZoomInstance() {
		// Sets zoom transition
		Zoom.setTransitionMode(
			switch (CONFIG.features.zoomTransition.value()) {
				case SMOOTH -> new SmoothTransitionMode(CONFIG.transitionValues.smoothTransitionFactor.value().floatValue());
				case LINEAR -> new LinearTransitionMode(CONFIG.transitionValues.minimumLinearStep.value(), CONFIG.transitionValues.maximumLinearStep.value());
				default -> new InstantTransitionMode();
			}
		);

		// Sets zoom divisor
		Zoom.setDefaultZoomDivisor(CONFIG.zoomValues.zoomDivisor.value());

		// Sets mouse modifier
		configureZoomModifier();

		// Sets zoom overlay
		// TODO - Restore the "Use Spyglass Texture" option as a "Use Custom Texture" option
		// You won't do it without a nice placeholder texture though (that isn't Michael lmfao)
		var overlayTextureId = CONFIG.features.zoomOverlay.value() == ConfigEnums.ZoomOverlays.SPYGLASS
			? ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png")
			: ModUtils.id("textures/misc/zoom_overlay.png");

		Zoom.setZoomOverlay(
			switch (CONFIG.features.zoomOverlay.value()) {
				case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
				case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
				default -> null;
			}
		);
	}

	public static void configureZoomModifier() {
		var cinematicCamera = CONFIG.features.cinematicCamera.value();
		boolean reduceSensitivity = CONFIG.features.reduceSensitivity.value();
		if (cinematicCamera != CinematicCameraOptions.OFF) {
			var cinematicModifier = switch (cinematicCamera) {
				case VANILLA -> new CinematicCameraMouseModifier();
				case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(CONFIG.zoomValues.cinematicMultiplier.value());
				default -> null;
			};
			Zoom.setMouseModifier(reduceSensitivity
				? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
				: cinematicModifier
			);
		} else {
			Zoom.setMouseModifier(reduceSensitivity ? new ZoomDivisorMouseModifier() : null);
		}
	}
}

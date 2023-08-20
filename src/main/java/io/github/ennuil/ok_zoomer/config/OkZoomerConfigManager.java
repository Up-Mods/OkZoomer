package io.github.ennuil.ok_zoomer.config;

import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.LinearTransitionMode;
import io.github.ennuil.ok_zoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.ZoomerZoomOverlay;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class OkZoomerConfigManager {
	public static final OkZoomerConfig CONFIG = QuiltConfig.create("ok_zoomer", "config", OkZoomerConfig.class);

	public OkZoomerConfigManager() {
		// On initialization, configure our zoom instance
		OkZoomerConfigManager.configureZoomInstance();

		CONFIG.registerCallback(config -> OkZoomerConfigManager.configureZoomInstance());
	}

	public static void configureZoomInstance() {
		// Sets zoom transition
		ZoomUtils.ZOOMER_ZOOM.setTransitionMode(
			switch (CONFIG.features.zoom_transition.value()) {
				case SMOOTH -> new SmoothTransitionMode(CONFIG.values.smooth_multiplier.value().floatValue());
				case LINEAR -> new LinearTransitionMode(CONFIG.values.minimum_linear_step.value(), CONFIG.values.maximum_linear_step.value());
				default -> new InstantTransitionMode();
			}
		);

		// Sets zoom divisor
		ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(CONFIG.values.zoom_divisor.value());

		// Sets mouse modifier
		configureZoomModifier();

		// Sets zoom overlay
		Identifier overlayTextureId = new Identifier(
			CONFIG.tweaks.use_spyglass_texture.value()
			? "textures/misc/spyglass_scope.png"
			: "ok_zoomer:textures/misc/zoom_overlay.png");

		ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(
			switch (CONFIG.features.zoom_overlay.value()) {
				case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
				case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
				default -> null;
			}
		);
	}

	public static void configureZoomModifier() {
		CinematicCameraOptions cinematicCamera = CONFIG.features.cinematic_camera.value();
		boolean reduceSensitivity = CONFIG.features.reduce_sensitivity.value();
		if (cinematicCamera != CinematicCameraOptions.OFF) {
			MouseModifier cinematicModifier = switch (cinematicCamera) {
				case VANILLA -> new CinematicCameraMouseModifier();
				case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(CONFIG.values.cinematic_multiplier.value());
				default -> null;
			};
			ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity
				? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
				: cinematicModifier
			);
		} else {
			ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity ? new ZoomDivisorMouseModifier() : null);
		}
	}
}

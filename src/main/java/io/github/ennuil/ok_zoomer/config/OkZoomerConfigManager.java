package io.github.ennuil.ok_zoomer.config;

import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.ok_zoomer.OkZoomerClientMod;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.LinearTransitionMode;
import io.github.ennuil.ok_zoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.ZoomerZoomOverlay;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class OkZoomerConfigManager {
	public static final OkZoomerConfig CONFIG = QuiltConfig.create(OkZoomerClientMod.MOD_ID, "config", OkZoomerConfig.class);

	public OkZoomerConfigManager() {
		// On initialization, configure our zoom instance
		OkZoomerConfigManager.configureZoomInstance();

		CONFIG.registerCallback(config -> OkZoomerConfigManager.configureZoomInstance());
	}

	public static void configureZoomInstance() {
		// Sets zoom transition
		ZoomUtils.ZOOMER_ZOOM.setTransitionMode(
			switch (CONFIG.features.zoomTransition.value()) {
				case SMOOTH -> new SmoothTransitionMode(CONFIG.transitionValues.smoothTransitionFactor.value().floatValue());
				case LINEAR -> new LinearTransitionMode(CONFIG.transitionValues.minimumLinearStep.value(), CONFIG.transitionValues.maximumLinearStep.value());
				default -> new InstantTransitionMode();
			}
		);

		// Sets zoom divisor
		ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(CONFIG.zoomValues.zoomDivisor.value());

		// Sets mouse modifier
		configureZoomModifier();

		// Sets zoom overlay
		ResourceLocation overlayTextureId = new ResourceLocation(
			CONFIG.tweaks.useSpyglassTexture.value()
			? "textures/misc/spyglass_scope.png"
			: "ok_zoomer:textures/misc/zoom_overlay.png");

		ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(
			switch (CONFIG.features.zoomOverlay.value()) {
				case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
				case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
				default -> null;
			}
		);
	}

	public static void configureZoomModifier() {
		CinematicCameraOptions cinematicCamera = CONFIG.features.cinematicCamera.value();
		boolean reduceSensitivity = CONFIG.features.reduceSensitivity.value();
		if (cinematicCamera != CinematicCameraOptions.OFF) {
			MouseModifier cinematicModifier = switch (cinematicCamera) {
				case VANILLA -> new CinematicCameraMouseModifier();
				case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(CONFIG.zoomValues.cinematicMultiplier.value());
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

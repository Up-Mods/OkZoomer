package page.langeweile.ok_zoomer.config;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import page.langeweile.ok_zoomer.utils.ModUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;
import page.langeweile.ok_zoomer.zoom.modifiers.CinematicCameraMouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.ContainingMouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.ZoomDivisorMouseModifier;
import page.langeweile.ok_zoomer.zoom.overlays.SpyglassZoomOverlay;
import page.langeweile.ok_zoomer.zoom.overlays.ZoomerZoomOverlay;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;
import page.langeweile.wrench_wrapper.api.WrenchWrapper;

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
			new EasedTransitionMode(
				getZoomTransitionOperator(OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionMode.value()),
				getZoomTransitionOperator(OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionMode.value()),
				getInwardZoomTransitionTicks(),
				getOutwardZoomTransitionTicks(),
				OkZoomerConfigManager.CONFIG.zoomTransition.invertStartTransition.value(),
				OkZoomerConfigManager.CONFIG.zoomTransition.invertEndTransition.value()
			)
		);

		// Sets mouse modifier
		OkZoomerConfigManager.configureZoomModifier();

		// Sets zoom overlay
		// TODO - Restore the "Use Spyglass Texture" option as a "Use Custom Texture" option
		// You won't do it without a nice placeholder texture though (that isn't Michael lmfao)
		var overlayTextureId = CONFIG.appearance.zoomOverlay.value() == ConfigEnums.ZoomOverlays.SPYGLASS
			? Identifier.withDefaultNamespace("textures/misc/spyglass_scope.png")
			: ModUtils.id("textures/misc/zoom_overlay.png");

		Zoom.setZoomOverlay(
			switch (CONFIG.appearance.zoomOverlay.value()) {
				case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
				case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
				default -> null;
			}
		);
	}

	public static FloatUnaryOperator getZoomTransitionOperator(ConfigEnums.ZoomTransitionModes mode) {
		return switch (mode) {
			case INSTANT -> f -> 1.0F;
			case LINEAR -> f -> f;
			case SMOOTH -> f -> (float) (1.0 - Math.pow(2.0, -10.0 * f));
			case SINE -> f -> (float) (1.0 - Math.cos(f * Math.PI / 2.0));
			case SPRING -> f -> (float) (Math.pow(2.0, -10.0F * f) * Mth.sin((f * 10.0F - 0.75F) * 1.5F) + 1.0F);
		};
	}

	public static int getInwardZoomTransitionTicks() {
		return OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionMode.value() != ConfigEnums.ZoomTransitionModes.INSTANT
			? OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionTicks.value()
			: 0;
	}

	public static int getOutwardZoomTransitionTicks() {
		return OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionMode.value() != ConfigEnums.ZoomTransitionModes.INSTANT
			? OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionTicks.value()
			: 0;
	}

	public static void configureZoomModifier() {
		boolean cinematicCamera = CONFIG.controls.cinematicCamera.value();
		boolean reduceSensitivity = CONFIG.controls.reduceSensitivity.value();
		if (cinematicCamera) {
			var cinematicModifier = new CinematicCameraMouseModifier(CONFIG.controls.cinematicCameraSpeed.value());

			Zoom.setMouseModifier(reduceSensitivity
				? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
				: cinematicModifier
			);
		} else {
			Zoom.setMouseModifier(reduceSensitivity ? new ZoomDivisorMouseModifier() : null);
		}
	}
}

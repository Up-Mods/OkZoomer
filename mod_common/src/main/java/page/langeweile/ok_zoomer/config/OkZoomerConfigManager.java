package page.langeweile.ok_zoomer.config;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import page.langeweile.ok_zoomer.utils.ModUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;
import page.langeweile.ok_zoomer.zoom.ZoomCore;
import page.langeweile.ok_zoomer.zoom.modifiers.CinematicCameraMouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.ContainingMouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.MouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.ZoomDivisorMouseModifier;
import page.langeweile.ok_zoomer.zoom.overlays.Overlay;
import page.langeweile.ok_zoomer.zoom.overlays.SpyglassOverlay;
import page.langeweile.ok_zoomer.zoom.overlays.VignetteOverlay;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;
import page.langeweile.wrench_wrapper.api.WrenchWrapper;

public class OkZoomerConfigManager {
	public static final OkZoomerConfig CONFIG = WrenchWrapper.create(ModUtils.MOD_NAMESPACE, "config", OkZoomerConfig.class);

	public static void init() {
		// On initialization, configure our zoom instance
		OkZoomerConfigManager.configureZoomInstance();

		CONFIG.registerCallback(_ -> OkZoomerConfigManager.configureZoomInstance());
	}

	public static void configureZoomInstance() {
		Zoom.setZoomCore(new ZoomCore(
			OkZoomerConfigManager.configureTransition(),
			OkZoomerConfigManager.configureMouseModifier(),
			OkZoomerConfigManager.configureOverlay()
		));
	}

	public static FloatUnaryOperator getTransitionOperator(ConfigEnums.ZoomTransitionModes mode) {
		return switch (mode) {
			case INSTANT -> _ -> 1.0F;
			case LINEAR -> f -> f;
			case SMOOTH -> Ease::outExpo;
			case SINE -> Ease::outSine;
			case BALANCED -> Ease::outCubic;
			// While the rest is out-of-shelf easings.net easings (yes, Mojang copied them), the Out Elastic easing has been modified to be less nausea-inducing
			case SPRING -> f -> (float) (Math.pow(2.0, -10.0F * f) * Mth.sin((f * 10.0F - 0.75F) * 1.5F) + 1.0F);
		};
	}

	public static int getStartTransitionTicks() {
		return OkZoomerConfigManager.CONFIG.zoomTransition.startTransition.value() != ConfigEnums.ZoomTransitionModes.INSTANT
			? OkZoomerConfigManager.CONFIG.zoomTransition.startTransitionTicks.value()
			: 0;
	}

	public static int getEndTransitionTicks() {
		return OkZoomerConfigManager.CONFIG.zoomTransition.endTransition.value() != ConfigEnums.ZoomTransitionModes.INSTANT
			? OkZoomerConfigManager.CONFIG.zoomTransition.endTransitionTicks.value()
			: 0;
	}

	public static int getScrollTransitionTicks() {
		return OkZoomerConfigManager.CONFIG.zoomScrolling.transition.value() != ConfigEnums.ZoomTransitionModes.INSTANT
			? OkZoomerConfigManager.CONFIG.zoomScrolling.transitionTicks.value()
			: 0;
	}

	public static EasedTransitionMode configureTransition() {
		return new EasedTransitionMode(
			OkZoomerConfigManager.getTransitionOperator(OkZoomerConfigManager.CONFIG.zoomTransition.startTransition.value()),
			OkZoomerConfigManager.getTransitionOperator(OkZoomerConfigManager.CONFIG.zoomTransition.endTransition.value()),
			OkZoomerConfigManager.getTransitionOperator(OkZoomerConfigManager.CONFIG.zoomScrolling.transition.value()),
			OkZoomerConfigManager.getStartTransitionTicks(),
			OkZoomerConfigManager.getEndTransitionTicks(),
			OkZoomerConfigManager.getScrollTransitionTicks(),
			OkZoomerConfigManager.CONFIG.zoomTransition.invertStartTransition.value(),
			OkZoomerConfigManager.CONFIG.zoomTransition.invertEndTransition.value()
		);
	}

	public static MouseModifier configureMouseModifier() {
		boolean cinematicCamera = CONFIG.controls.cinematicCamera.value();
		boolean reduceSensitivity = CONFIG.controls.reduceSensitivity.value();
		if (cinematicCamera) {
			var cinematicModifier = new CinematicCameraMouseModifier(CONFIG.controls.cinematicCameraSpeed.value());

			return reduceSensitivity
				? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
				: cinematicModifier;
		} else {
			return reduceSensitivity ? new ZoomDivisorMouseModifier() : null;
		}
	}

	public static Overlay configureOverlay() {
		// TODO - Restore the "Use Spyglass Texture" option as a new mode
		var overlayTextureId = CONFIG.appearance.zoomOverlay.value() == ConfigEnums.ZoomOverlays.SPYGLASS
			? Identifier.withDefaultNamespace("textures/misc/spyglass_scope.png")
			: ModUtils.id("textures/misc/zoom_overlay.png");

		return switch (CONFIG.appearance.zoomOverlay.value()) {
			case VIGNETTE -> new VignetteOverlay(overlayTextureId);
			case SPYGLASS -> new SpyglassOverlay(overlayTextureId);
			default -> null;
		};
	}
}

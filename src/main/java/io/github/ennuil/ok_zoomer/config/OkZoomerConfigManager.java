package io.github.ennuil.ok_zoomer.config;

import java.util.List;

import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.QuiltConfig;

import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.SpyglassDependency;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.LinearTransitionMode;
import io.github.ennuil.ok_zoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.ZoomerZoomOverlay;
import net.minecraft.util.Identifier;

@SuppressWarnings("unchecked")
public class OkZoomerConfigManager {
	public static final OkZoomerConfig CONFIG = QuiltConfig.create("ok_zoomer", "config", OkZoomerConfig.class);

	// Features
	public static final TrackedValue<CinematicCameraOptions> CINEMATIC_CAMERA = (TrackedValue<CinematicCameraOptions>) CONFIG.getValue(List.of("features", "cinematic_camera"));
	public static final TrackedValue<Boolean> REDUCE_SENSITIVITY = (TrackedValue<Boolean>) CONFIG.getValue(List.of("features", "reduce_sensitivity"));
	public static final TrackedValue<ZoomTransitionOptions> ZOOM_TRANSITION = (TrackedValue<ZoomTransitionOptions>) CONFIG.getValue(List.of("features", "zoom_transition"));
	public static final TrackedValue<ZoomModes> ZOOM_MODE = (TrackedValue<ZoomModes>) CONFIG.getValue(List.of("features", "zoom_mode"));
	public static final TrackedValue<Boolean> ZOOM_SCROLLING = (TrackedValue<Boolean>) CONFIG.getValue(List.of("features", "zoom_scrolling"));
	public static final TrackedValue<Boolean> EXTRA_KEY_BINDS = (TrackedValue<Boolean>) CONFIG.getValue(List.of("features", "extra_key_binds"));
	public static final TrackedValue<ZoomOverlays> ZOOM_OVERLAY = (TrackedValue<ZoomOverlays>) CONFIG.getValue(List.of("features", "zoom_overlay"));
	public static final TrackedValue<SpyglassDependency> SPYGLASS_DEPENDENCY = (TrackedValue<SpyglassDependency>) CONFIG.getValue(List.of("features", "spyglass_dependency"));

	// Values
	public static final TrackedValue<Double> ZOOM_DIVISOR = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "zoom_divisor"));
	public static final TrackedValue<Double> MINIMUM_ZOOM_DIVISOR = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "minimum_zoom_divisor"));
	public static final TrackedValue<Double> MAXIMUM_ZOOM_DIVISOR = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "maximum_zoom_divisor"));
	public static final TrackedValue<Integer> UPPER_SCROLL_STEPS = (TrackedValue<Integer>) CONFIG.getValue(List.of("values", "upper_scroll_steps"));
	public static final TrackedValue<Integer> LOWER_SCROLL_STEPS = (TrackedValue<Integer>) CONFIG.getValue(List.of("values", "lower_scroll_steps"));
	public static final TrackedValue<Double> SMOOTH_MULTIPLIER = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "smooth_multiplier"));
	public static final TrackedValue<Double> CINEMATIC_MULTIPLIER = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "cinematic_multiplier"));
	public static final TrackedValue<Double> MINIMUM_LINEAR_STEP = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "minimum_linear_step"));
	public static final TrackedValue<Double> MAXIMUM_LINEAR_STEP = (TrackedValue<Double>) CONFIG.getValue(List.of("values", "maximum_linear_step"));

	// Tweaks
	public static final TrackedValue<Boolean> RESET_ZOOM_WITH_MOUSE = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "reset_zoom_with_mouse"));
	public static final TrackedValue<Boolean> FORGET_ZOOM_DIVISOR = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "forget_zoom_divisor"));
	public static final TrackedValue<Boolean> UNBIND_CONFLICTING_KEY = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "unbind_conflicting_key"));
	public static final TrackedValue<Boolean> USE_SPYGLASS_TEXTURE = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "use_spyglass_texture"));
	public static final TrackedValue<Boolean> USE_SPYGLASS_SOUNDS = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "use_spyglass_sounds"));
	public static final TrackedValue<Boolean> SHOW_RESTRICTION_TOASTS = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "show_restriction_toasts"));
	public static final TrackedValue<Boolean> PRINT_OWO_ON_START = (TrackedValue<Boolean>) CONFIG.getValue(List.of("tweaks", "print_owo_on_start"));


	public OkZoomerConfigManager() {
		CONFIG.registerCallback(config -> {
			OkZoomerConfigManager.configureZoomInstance();
		});
	}

	public static void resetToPreset(ZoomPresets preset) {
		CINEMATIC_CAMERA.setValue(preset == ZoomPresets.CLASSIC ? CinematicCameraOptions.VANILLA : CinematicCameraOptions.OFF, false);
		REDUCE_SENSITIVITY.setValue(preset == ZoomPresets.CLASSIC ? false : true, false);
		ZOOM_TRANSITION.setValue(preset == ZoomPresets.CLASSIC ? ZoomTransitionOptions.OFF : ZoomTransitionOptions.SMOOTH, false);
		ZOOM_MODE.setValue(preset == ZoomPresets.PERSISTENT ? ZoomModes.PERSISTENT : ZoomModes.HOLD, false);
		ZOOM_SCROLLING.setValue(switch (preset) {
			case CLASSIC -> false;
			case SPYGLASS -> false;
			default -> true;
		}, false);
		EXTRA_KEY_BINDS.setValue(preset == ZoomPresets.CLASSIC ? false : true, false);
		ZOOM_OVERLAY.setValue(preset == ZoomPresets.SPYGLASS ? ZoomOverlays.SPYGLASS : ZoomOverlays.OFF, false);
		SPYGLASS_DEPENDENCY.setValue(preset == ZoomPresets.SPYGLASS ? SpyglassDependency.BOTH : SpyglassDependency.OFF, false);

		ZOOM_DIVISOR.setValue(switch (preset) {
			case PERSISTENT -> 1.0D;
			case SPYGLASS -> 10.0D;
			default -> 4.0D;
		}, false);
		MINIMUM_ZOOM_DIVISOR.setValue(1.0D, false);
		MAXIMUM_ZOOM_DIVISOR.setValue(50.0D, false);
		UPPER_SCROLL_STEPS.setValue(preset == ZoomPresets.SPYGLASS ? 16 : 20, false);
		LOWER_SCROLL_STEPS.setValue(preset == ZoomPresets.SPYGLASS ? 8 : 4, false);
		SMOOTH_MULTIPLIER.setValue(preset == ZoomPresets.SPYGLASS ? 0.5D : 0.75D, false);
		CINEMATIC_MULTIPLIER.setValue(4.0D, false);
		MINIMUM_LINEAR_STEP.setValue(0.125D, false);
		MAXIMUM_LINEAR_STEP.setValue(0.25D, false);

		RESET_ZOOM_WITH_MOUSE.setValue(preset == ZoomPresets.CLASSIC ? false : true, false);
		UNBIND_CONFLICTING_KEY.setValue(false, false);
		USE_SPYGLASS_TEXTURE.setValue(preset == ZoomPresets.SPYGLASS ? true : false, false);
		USE_SPYGLASS_SOUNDS.setValue(preset == ZoomPresets.SPYGLASS ? true : false, false);
		SHOW_RESTRICTION_TOASTS.setValue(true, false);
		PRINT_OWO_ON_START.setValue(preset == ZoomPresets.CLASSIC ? false : true, false);

		CONFIG.save();
	}

	// TODO - Use Quilt Config's Override system
	public static void configureZoomInstance() {
		// Sets zoom transition
		ZoomUtils.ZOOMER_ZOOM.setTransitionMode(
			switch (ZOOM_TRANSITION.value()) {
				case SMOOTH -> new SmoothTransitionMode(SMOOTH_MULTIPLIER.value().floatValue());
				case LINEAR -> new LinearTransitionMode(MINIMUM_LINEAR_STEP.value(), MAXIMUM_LINEAR_STEP.value());
				default -> new InstantTransitionMode();
			}
		);

		// Forces Classic Mode settings
		if (ZoomPackets.getForceClassicMode()) {
			ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(4.0D);
			ZoomUtils.ZOOMER_ZOOM.setMouseModifier(new CinematicCameraMouseModifier());
			ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(null);
			return;
		}

		// Sets zoom divisor
		ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(ZOOM_DIVISOR.value());

		// Sets mouse modifier
		configureZoomModifier();

		// Sets zoom overlay
		Identifier overlayTextureId = new Identifier(
			USE_SPYGLASS_TEXTURE.value()
			? "textures/misc/spyglass_scope.png"
			: "ok_zoomer:textures/misc/zoom_overlay.png");

		// Enforce spyglass overlay if necessary
		ZoomOverlays overlay = ZoomPackets.getSpyglassOverlay() ? ZoomOverlays.SPYGLASS : ZOOM_OVERLAY.value();

		ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(
			switch (overlay) {
				case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
				case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
				default -> null;
			}
		);
	}

	public static void configureZoomModifier() {
		CinematicCameraOptions cinematicCamera = CINEMATIC_CAMERA.value();
		boolean reduceSensitivity = REDUCE_SENSITIVITY.value();
		if (cinematicCamera != CinematicCameraOptions.OFF) {
			MouseModifier cinematicModifier = switch (cinematicCamera) {
				case VANILLA -> new CinematicCameraMouseModifier();
				case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(CINEMATIC_MULTIPLIER.value());
				default -> null;
			};
			ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity
				? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
				: cinematicModifier
			);
		} else {
			ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity
				? new ZoomDivisorMouseModifier()
				: null
			);
		}
	}

	public enum ZoomPresets {
		DEFAULT,
		CLASSIC,
		PERSISTENT,
		SPYGLASS
	}
}

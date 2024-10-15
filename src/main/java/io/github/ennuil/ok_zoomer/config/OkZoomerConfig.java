package io.github.ennuil.ok_zoomer.config;

import io.github.ennuil.ok_zoomer.config.ConfigEnums.*;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize.Size;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.*;
import org.quiltmc.config.api.metadata.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class OkZoomerConfig extends ReflectiveConfig {
	@Comment("Allows for configuring the main zoom features.")
	public final FeaturesConfig features = new FeaturesConfig();

	@Alias("values")
	@Comment("Allows for precise tweaking of the zoom.")
	public final ZoomValuesConfig zoomValues = new ZoomValuesConfig();

	@Alias("values")
	@Comment("Allows for precise tweaking of zoom transitions.")
	public final TransitionValuesConfig transitionValues = new TransitionValuesConfig();

	@Comment("Provides a selection of unusual options.")
	public final TweaksConfig tweaks = new TweaksConfig();

	public static class FeaturesConfig extends Section {
		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": Disables the zoom's cinematic camera.
			"VANILLA": Uses the game's cinematic camera while zooming.
			"MULTIPLIED": Uses the cinematic camera with a configurable multiplier while zooming.
			""")
		public final TrackedValue<CinematicCameraOptions> cinematicCamera = this.value(CinematicCameraOptions.OFF);

		@WidgetSize(Size.HALF)
		@Comment("Divides the mouse sensitivity with the zoom divisor while zooming.")
		public final TrackedValue<Boolean> reduceSensitivity = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": The zoom will abruptly transition between its on and off states.
			"SMOOTH": The zoom will smoothly transition between its on and off states in a manner resembling the game's FOV transitions.
			"LINEAR": The zoom will linearly transition between its on and off states.
			""")
		public final TrackedValue<ZoomTransitionOptions> zoomTransition = this.value(ZoomTransitionOptions.SMOOTH);

		@WidgetSize(Size.HALF)
		@Comment("Divides the amount of view bobbing with the zoom divisor while zooming.")
		public final TrackedValue<Boolean> reduceViewBobbing = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"HOLD": The zoom will require the zoom key to be held.
			"TOGGLE": The zoom will be toggled by the zoom key.
			"PERSISTENT": The zoom will always be enabled, with the zoom key being used for zoom scrolling.
			""")
		public final TrackedValue<ZoomModes> zoomMode = this.value(ZoomModes.HOLD);

		@WidgetSize(Size.HALF)
		@Comment("Allows to increase or decrease the zoom by scrolling with the mouse wheel.")
		public final TrackedValue<Boolean> zoomScrolling = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Retains the interface when zooming.")
		public final TrackedValue<Boolean> persistentInterface = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Adds zoom manipulation keys along with the zoom key. A game reboot will be required in order to apply the changes.")
		public final TrackedValue<Boolean> extraKeyBinds = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": Disables the zoom overlay.
			"VIGNETTE": Uses a vignette as the zoom overlay. The vignette texture can be found at assets/ok_zoomer/textures/misc/zoom_overlay.png
			"SPYGLASS": Uses the spyglass overlay with the vignette texture.
			""")
		public final TrackedValue<ZoomOverlays> zoomOverlay = this.value(ZoomOverlays.OFF);

		@WidgetSize(Size.HALF)
		@Alias("spyglass_dependency")
		@Comment("""
			"OFF": Zooming won't require a spyglass and won't replace its zoom.
			"REQUIRE_ITEM": Zooming will require a spyglass in order to work. This option is configurable through the ok_zoomer:zoom_dependencies item tag.
			"REPLACE_ZOOM": Zooming will replace the spyglass zoom but it won't require one in order to work.
			"BOTH": Zooming will act as a complete replacement of the spyglass zoom, requiring one to work and replacing its zoom as well.
			""")
		public final TrackedValue<SpyglassMode> spyglassMode = this.value(SpyglassMode.OFF);
	}

	public static class ZoomValuesConfig extends Section  {
		@WidgetSize(Size.HALF)
		@Comment("The divisor applied to the FOV's zoom multiplier. A higher value means more zoom.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> zoomDivisor = this.value(4.0);

		@WidgetSize(Size.HALF)
		@Comment("The minimum zoom divisor that you can scroll down.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> minimumZoomDivisor = this.value(1.0);

		@WidgetSize(Size.HALF)
		@Comment("The maximum zoom divisor that you can scroll up.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> maximumZoomDivisor = this.value(50.0);

		@WidgetSize(Size.HALF)
		@Comment("The number of steps between the zoom divisor and the minimum zoom divisor. Used by zoom scrolling.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		public final TrackedValue<Integer> lowerScrollSteps = this.value(5);

		@WidgetSize(Size.HALF)
		@Comment("The number of steps between the zoom divisor and the maximum zoom divisor. Used by zoom scrolling.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		public final TrackedValue<Integer> upperScrollSteps = this.value(10);

		@WidgetSize(Size.HALF)
		@Comment("The multiplier used for the multiplied cinematic camera.")
		@FloatRange(min = Double.MIN_NORMAL, max = 32.0)
		public final TrackedValue<Double> cinematicMultiplier = this.value(4.0);
	}

	public static class TransitionValuesConfig extends Section  {
		@WidgetSize(Size.HALF)
		@Comment("The factor used for smooth zoom transitions. A lower value means a smoother transition, a higher value means a faster one.")
		@Alias("smooth_multiplier")
		@FloatRange(min = Double.MIN_NORMAL, max = 1.0)
		public final TrackedValue<Double> smoothTransitionFactor = this.value(0.6);

		@WidgetSize(Size.HALF)
		@Comment("The minimum value which the linear zoom transition step can reach.")
		@FloatRange(min = 0.0, max = Double.MAX_VALUE)
		public final TrackedValue<Double> minimumLinearStep = this.value(0.16);

		@WidgetSize(Size.HALF)
		@Comment("The maximum value which the linear zoom transition step can reach.")
		@FloatRange(min = 0.0, max = Double.MAX_VALUE)
		public final TrackedValue<Double> maximumLinearStep = this.value(0.22);
	}

	public static class TweaksConfig extends Section  {
		@WidgetSize(Size.FULL)
		@Comment("If pressed, the \"Save Toolbar Activator\" keybind will be unbound if there's a conflict with the zoom key.")
		public final TrackedValue<Boolean> unbindConflictingKey = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Hides the crosshair while zooming.")
		public final TrackedValue<Boolean> hideCrosshair = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Allows for resetting the zoom with the middle mouse button.")
		public final TrackedValue<Boolean> resetZoomWithMouse = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the current zoom divisor is forgotten once zooming is finished.")
		public final TrackedValue<Boolean> forgetZoomDivisor = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the zoom will use spyglass sounds on zooming in and out.")
		public final TrackedValue<Boolean> useSpyglassSounds = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Shows toasts when the server imposes a restriction.")
		public final TrackedValue<Boolean> showRestrictionToasts = this.value(true);

		// TODO - Disable it upon stable release!
		@WidgetSize(Size.HALF)
		@Comment("Prints a random owo in the console when the game starts.")
		public final TrackedValue<Boolean> printOwoOnStart = this.value(true);
	}

	// TODO - What if we had a secret Debug section?
}

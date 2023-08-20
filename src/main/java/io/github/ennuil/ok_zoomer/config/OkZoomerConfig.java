package io.github.ennuil.ok_zoomer.config;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.FloatRange;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.values.TrackedValue;

import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.SpyglassDependency;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize.Size;

public class OkZoomerConfig extends ReflectiveConfig {
	@Comment("Contains the main zoom features.")
	public final FeaturesConfig features = new FeaturesConfig();

	@Comment("Contains precise configuration of the zoom.")
	public final ValuesConfig values = new ValuesConfig();

	@Comment("Contains options that are unlikely to be changed from the defaults.")
	public final TweaksConfig tweaks = new TweaksConfig();

	public static class FeaturesConfig extends Section {
		@WidgetSize(Size.HALF)
		@Comment("""
			Defines the cinematic camera while zooming.
			"OFF" disables the cinematic camera.
			"VANILLA" uses Vanilla's cinematic camera.
			"MULTIPLIED" is a multiplied variant of "VANILLA".
			""")
		public final TrackedValue<CinematicCameraOptions> cinematic_camera = this.value(CinematicCameraOptions.OFF);

		@WidgetSize(Size.HALF)
		@Comment("Reduces the mouse sensitivity when zooming.")
		public final TrackedValue<Boolean> reduce_sensitivity = this.value(true);

		@WidgetSize(Size.FULL)
		@Comment("""
			Adds transitions between zooms.
			"OFF" disables transitions.
			"SMOOTH" replicates Vanilla's dynamic FOV.
			"LINEAR" removes the smoothiness.
			""")
		public final TrackedValue<ZoomTransitionOptions> zoom_transition = this.value(ZoomTransitionOptions.SMOOTH);

		@WidgetSize(Size.HALF)
		@Comment("""
			The behavior of the zoom key.
			"HOLD" needs the zoom key to be hold.
			"TOGGLE" has the zoom key toggle the zoom.
			"PERSISTENT" makes the zoom permanent.
			""")
		public final TrackedValue<ZoomModes> zoom_mode = this.value(ZoomModes.HOLD);

		@WidgetSize(Size.HALF)
		@Comment("Allows to increase or decrease zoom by scrolling.")
		public final TrackedValue<Boolean> zoom_scrolling = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Adds zoom manipulation keys along with the zoom key.")
		public final TrackedValue<Boolean> extra_key_binds = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			Adds an overlay in the screen during zoom.
			"VIGNETTE" uses a vignette as the overlay.
			"SPYGLASS" uses the spyglass overlay with the vignette texture.
			The vignette texture can be found at: assets/ok_zoomer/textures/misc/zoom_overlay.png
			""")
		public final TrackedValue<ZoomOverlays> zoom_overlay = this.value(ZoomOverlays.OFF);

		@WidgetSize(Size.FULL)
		@Comment("""
			Determines how the zoom will depend on the spyglass.
			"REQUIRE_ITEM" will make zooming require a spyglass.
			"REPLACE_ZOOM" will replace spyglass's zoom with Ok Zoomer's zoom.
			"BOTH" will apply both options at the same time.
			The "REQUIRE_ITEM" option is configurable through the ok_zoomer:zoom_dependencies item tag.
			""")
		public final TrackedValue<SpyglassDependency> spyglass_dependency = this.value(SpyglassDependency.OFF);
	}

	public static class ValuesConfig extends Section  {
		@WidgetSize(Size.FULL)
		@Comment("The divisor applied to the FOV when zooming.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> zoom_divisor = this.value(4.0);

		@WidgetSize(Size.HALF)
		@Comment("The minimum value that you can scroll down.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> minimum_zoom_divisor = this.value(1.0);

		@WidgetSize(Size.HALF)
		@Comment("The maximum value that you can scroll down.")
		@FloatRange(min = Double.MIN_NORMAL, max = Double.MAX_VALUE)
		public final TrackedValue<Double> maximum_zoom_divisor = this.value(50.0);

		@WidgetSize(Size.HALF)
		@Comment("""
			The number of steps between the zoom divisor and the maximum zoom divisor.
			Used by zoom scrolling.
			""")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		public final TrackedValue<Integer> upper_scroll_steps = this.value(10);

		@WidgetSize(Size.HALF)
		@Comment("""
			The number of steps between the zoom divisor and the minimum zoom divisor.
			Used by zoom scrolling.
			""")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		public final TrackedValue<Integer> lower_scroll_steps = this.value(5);

		@WidgetSize(Size.HALF)
		@Comment("The multiplier used for smooth transitions.")
		@FloatRange(min = Double.MIN_NORMAL, max = 1.0)
		public final TrackedValue<Double> smooth_multiplier = this.value(0.75);

		@WidgetSize(Size.HALF)
		@Comment("The multiplier used for the multiplied cinematic camera.")
		@FloatRange(min = Double.MIN_NORMAL, max = 32.0)
		public final TrackedValue<Double> cinematic_multiplier = this.value(4.0);

		@WidgetSize(Size.HALF)
		@Comment("The minimum value which the linear transition step can reach.")
		@FloatRange(min = 0.0, max = Double.MAX_VALUE)
		public final TrackedValue<Double> minimum_linear_step = this.value(0.125);

		@WidgetSize(Size.HALF)
		@Comment("The maximum value which the linear transition step can reach.")
		@FloatRange(min = 0.0, max = Double.MAX_VALUE)
		public final TrackedValue<Double> maximum_linear_step = this.value(0.25);
	}

	public static class TweaksConfig extends Section  {
		@WidgetSize(Size.HALF)
		@Comment("Allows for resetting the zoom with the middle mouse button.")
		public final TrackedValue<Boolean> reset_zoom_with_mouse = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the current zoom divisor is forgotten once zooming is done.")
		public final TrackedValue<Boolean> forget_zoom_divisor = this.value(true);

		@WidgetSize(Size.FULL)
		@Comment("If pressed, the \"Save Toolbar Activator\" keybind will be unbound if there's a conflict with the zoom key.")
		public final TrackedValue<Boolean> unbind_conflicting_key = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the spyglass overlay texture is used instead of Ok Zoomer's overlay texture.")
		public final TrackedValue<Boolean> use_spyglass_texture = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the zoom will use spyglass sounds on zooming in and out.")
		public final TrackedValue<Boolean> use_spyglass_sounds = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Shows toasts when the server imposes a restriction.")
		public final TrackedValue<Boolean> show_restriction_toasts = this.value(true);

		// TODO - Revert default value on 5.0.0
		@WidgetSize(Size.HALF)
		@Comment("Prints a random owo in the console when the game starts. Enabled by default until full release.")
		public final TrackedValue<Boolean> print_owo_on_start = this.value(true);
	}

	// TODO - What if we had a secret Debug section?
}

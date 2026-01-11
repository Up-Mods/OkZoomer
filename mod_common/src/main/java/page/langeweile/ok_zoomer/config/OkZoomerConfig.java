package page.langeweile.ok_zoomer.config;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.FloatRange;
import org.quiltmc.config.api.annotations.IntegerRange;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingSchemes;
import org.quiltmc.config.api.values.TrackedValue;
import page.langeweile.ok_zoomer.config.ConfigEnums.*;
import page.langeweile.ok_zoomer.config.metadata.RangeSubset;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize.Size;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class OkZoomerConfig extends ReflectiveConfig {
	@Comment("Options affecting the transitions between zooming in and zooming out.")
	public final ZoomTransitionConfig zoomTransition = new ZoomTransitionConfig();

	@Comment("Options affecting the visual aspects of zooming.")
	public final AppearanceConfig appearance = new AppearanceConfig();

	@Comment("Options affecting the way zooming is controlled.")
	public final ControlsConfig controls = new ControlsConfig();

	@Comment("Options affecting the Zoom Scrolling feature.")
	public final ZoomScrollingConfig zoomScrolling = new ZoomScrollingConfig();

	@Comment("Technical options that don't fit elsewhere.")
	public final TweaksConfig tweaks = new TweaksConfig();

	public static final class ZoomTransitionConfig extends Section {
		// TODO - These should resemble the other enum comments
		@WidgetSize(Size.HALF)
		@Comment("Determines which animation curve to use when zooming in.")
		public final TrackedValue<ZoomTransitionModes> startTransitionMode = this.value(ZoomTransitionModes.SMOOTH);

		@WidgetSize(Size.HALF)
		@Comment("Determines which animation curve to use when zooming out.")
		public final TrackedValue<ZoomTransitionModes> endTransitionMode = this.value(ZoomTransitionModes.SMOOTH);

		@WidgetSize(Size.HALF)
		@Comment("How long should the end transition last for. 1 second is equal to 20 ticks.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		@RangeSubset(min = 0, max = 100)
		public final TrackedValue<Integer> startTransitionTicks = this.value(8);

		@WidgetSize(Size.HALF)
		@Comment("How long should the start transition last for. 1 second is equal to 20 ticks.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		@RangeSubset(min = 0, max = 100)
		public final TrackedValue<Integer> endTransitionTicks = this.value(8);

		@WidgetSize(Size.HALF)
		@Comment("Inverts the start transition in a temporal way.")
		public final TrackedValue<Boolean> invertStartTransition = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Inverts the end transition in a temporal way.")
		public final TrackedValue<Boolean> invertEndTransition = this.value(false);
	}

	public static final class AppearanceConfig extends Section {
		@WidgetSize(Size.HALF)
		@Comment("Retains the interface when zooming.")
		public final TrackedValue<Boolean> persistentInterface = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Hides the crosshair while zooming.")
		public final TrackedValue<Boolean> hideCrosshair = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Divides the amount of view bobbing with the zoom divisor while zooming.")
		public final TrackedValue<Boolean> reduceViewBobbing = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Zooms the hand when zooming.")
		public final TrackedValue<Boolean> zoomHands = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": Disables the zoom overlay.
			"VIGNETTE": Uses a vignette as the zoom overlay. The vignette texture can be found at assets/ok_zoomer/textures/misc/zoom_overlay.png
			"SPYGLASS": Uses the spyglass overlay as the zoom overlay.
			""")
		public final TrackedValue<ZoomOverlays> zoomOverlay = this.value(ZoomOverlays.OFF);

		@WidgetSize(Size.HALF)
		@Comment("Improves performance by making the game render less of the world while zoomed in. This feature depends on the Sodium mod in order to work.")
		public final TrackedValue<Boolean> smartOcclusion = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Expands the entity distance while zooming in, allowing creatures and certain blocks to be seen from afar. This may have a performance impact during zoom.")
		public final TrackedValue<SeeDistantEntitiesModes> seeDistantEntities = this.value(SeeDistantEntitiesModes.SAFE);
	}

	public static final class ControlsConfig extends Section {
		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": Disables the zoom's cinematic camera.
			"VANILLA": Uses the game's cinematic camera while zooming.
			"MULTIPLIED": Uses the cinematic camera with a configurable multiplier while zooming.
			""")
		public final TrackedValue<Boolean> cinematicCamera = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("The multiplier used by the multiplied cinematic camera.")
		@FloatRange(min = Float.MIN_NORMAL, max = 32.0F)
		@RangeSubset(min = 1, max = 16)
		public final TrackedValue<Float> cinematicCameraSpeed = this.value(1.0F);

		@WidgetSize(Size.HALF)
		@Comment("Divides the mouse sensitivity with the zoom divisor while zooming.")
		public final TrackedValue<Boolean> reduceSensitivity = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"HOLD": The zoom will require the zoom key to be held.
			"TOGGLE": The zoom will be toggled by the zoom key.
			"PERSISTENT": The zoom will always be enabled, with the zoom key being used for zoom scrolling.
			""")
		public final TrackedValue<ZoomModes> zoomMode = this.value(ZoomModes.HOLD);

		@WidgetSize(Size.HALF)
		@Comment("Adds the spyglass's sounds effects on zooming in and out")
		public final TrackedValue<Boolean> spyglassSounds = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("Adds zoom manipulation keys along with the zoom key. A game reboot will be required in order to apply the changes.")
		public final TrackedValue<Boolean> extraKeyBinds = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("""
			"OFF": Zooming won't require a spyglass and won't replace its zoom.
			"REQUIRE_ITEM": Zooming will require a spyglass in order to work. This option is configurable through the ok_zoomer:zoom_dependencies item tag.
			"REPLACE_ZOOM": Zooming will replace the spyglass zoom but it won't require one in order to work.
			"BOTH": Zooming will act as a complete replacement of the spyglass zoom, requiring one to work and replacing its zoom as well.
			""")
		public final TrackedValue<SpyglassModes> spyglassMode = this.value(SpyglassModes.OFF);
	}

	public static final class ZoomScrollingConfig extends Section {
		@WidgetSize(Size.HALF)
		@Comment("Allows to increase or decrease the zoom by scrolling with the mouse wheel.")
		public final TrackedValue<Boolean> zoomScrolling = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Allows for resetting the zoom with the middle mouse button.")
		public final TrackedValue<Boolean> resetZoomWithMouse = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Adds sound effects on zoom scrolling.")
		public final TrackedValue<Boolean> scrollSounds = this.value(false);

		@WidgetSize(Size.HALF)
		@Comment("If enabled, the current scroll step is forgotten once zooming is finished.")
		public final TrackedValue<Boolean> forgetScrollStep = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Determines the number to be used on the exponential curve. If unsure, keep this value at 2.")
		@IntegerRange(min = 2, max = Integer.MAX_VALUE)
		@RangeSubset(min = 2, max = 10)
		public final TrackedValue<Integer> scrollBase = this.value(2);

		@WidgetSize(Size.HALF)
		@Comment("Determines the resolution of zoom scrolling. This will effectively multiply the amount of scroll steps.")
		@IntegerRange(min = 1, max = Integer.MAX_VALUE)
		@RangeSubset(min = 1, max = 20)
		public final TrackedValue<Integer> scrollResolution = this.value(5);

		@WidgetSize(Size.HALF)
		@Comment("The default scroll step to use on zooming in.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		@RangeSubset(min = 0, max = 100)
		public final TrackedValue<Integer> defaultScrollStep = this.value(10);

		@WidgetSize(Size.HALF)
		@Comment("The maximum amount of scroll steps that the zoom may reach.")
		@IntegerRange(min = 0, max = Integer.MAX_VALUE)
		@RangeSubset(min = 0, max = 100)
		public final TrackedValue<Integer> scrollStepLimit = this.value(30);
	}

	public static final class TweaksConfig extends Section  {
		@WidgetSize(Size.HALF)
		@Comment("a")
		public final TrackedValue<Boolean> numericSliders = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Adds a button to open Ok Zoomer settings next to the zoom key bind.")
		public final TrackedValue<Boolean> showSettingsOnKey = this.value(true);

		@WidgetSize(Size.HALF)
		@Comment("Displays debug information for exponential zoom scrolling. Currently it may help with configuring the zoom scrolling.")
		public final TrackedValue<Boolean> debugScrolling = this.value(false);

		// TODO - Disable it upon stable release!
		@WidgetSize(Size.HALF)
		@Comment("Prints a random owo in the console when the game starts.")
		public final TrackedValue<Boolean> printOwoOnStart = this.value(true);
	}

	// TODO - What if we had a secret Debug section?
}

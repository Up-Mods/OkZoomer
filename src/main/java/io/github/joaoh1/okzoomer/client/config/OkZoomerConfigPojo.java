package io.github.joaoh1.okzoomer.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Group;

public class OkZoomerConfigPojo {
	@Group()
	public static FeaturesGroup features = new FeaturesGroup();

	public static class FeaturesGroup {
		@Setting(comment = "Defines the cinematic camera while zooming.\n\"OFF\" disables the cinematic camera.\n\"VANILLA\" uses Vanilla's cinematic camera.\n\"MULTIPLIED\" is a multiplied variant of \"VANILLA\".")
		public CinematicCameraOptions cinematicCamera = CinematicCameraOptions.OFF;
	
		public enum CinematicCameraOptions {
			OFF,
			VANILLA,
			MULTIPLIED
		}
	
		@Setting(comment = "Reduces the mouse sensitivity when zooming.")
		public boolean reduceSensitivity = true;
	
		@Setting(comment = "Adds transitions between zooms.\n\"OFF\" disables transitions.\n\"SMOOTH\" replicates Vanilla's dynamic FOV.\n\"SINE\" applies the transition with a sine function.")
		public ZoomTransitionOptions zoomTransition = ZoomTransitionOptions.SMOOTH;
	
		public enum ZoomTransitionOptions {
			OFF,
			SMOOTH,
			SINE
		}
	
		@Setting(comment = "The behavior of the zoom key.\n\"HOLD\" needs the zoom key to be hold.\n\"TOGGLE\" has the zoom key toggle the zoom.\n\"PERSISTENT\" makes the zoom permanent.")
		public ZoomModes zoomMode = ZoomModes.HOLD;
	
		public enum ZoomModes {
			HOLD,
			TOGGLE,
			PERSISTENT
		}
	
		@Setting(comment = "Allows to increase or decrease zoom by scrolling.\nThe functionality might not be final.")
		public boolean zoomScrolling = true;
	}

	@Group()
	public static ValuesGroup values = new ValuesGroup();

	public static class ValuesGroup {
		@Setting.Constrain.Range(min = Double.MIN_NORMAL)
		@Setting(comment = "The divisor applied to the FOV when zooming.")
		public double zoomDivisor = 4.0;

		@Setting.Constrain.Range(min = Double.MIN_NORMAL)
		@Setting(comment = "The minimum value that you can scroll down.")
		public double minimumZoomDivisor = 1.0;
	
		@Setting.Constrain.Range(min = Double.MIN_NORMAL)
		@Setting(comment = "The maximum value that you can scroll up.")
		public double maximumZoomDivisor = 50.0;

		@Setting.Constrain.Range(min = Double.MIN_NORMAL)
		@Setting(comment = "The multiplier used for the multiplied cinematic camera.")
		public double cinematicMultiplier = 4.0;

		@Setting.Constrain.Range(min = Double.MIN_NORMAL, max = 1.0)
		@Setting(comment = "The multiplier used for smooth transitions.")
		public double smoothMultiplier = 0.75;
	}
}
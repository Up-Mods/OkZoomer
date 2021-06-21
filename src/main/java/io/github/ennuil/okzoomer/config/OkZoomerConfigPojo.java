package io.github.ennuil.okzoomer.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Group;

public class OkZoomerConfigPojo {
    @Group
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
    
        @Setting(comment = "Adds transitions between zooms.\n\"OFF\" disables transitions.\n\"SMOOTH\" replicates Vanilla's dynamic FOV.\n\"LINEAR\" removes the smoothiness.")
        public ZoomTransitionOptions zoomTransition = ZoomTransitionOptions.SMOOTH;
    
        public enum ZoomTransitionOptions {
            OFF,
            SMOOTH,
            LINEAR
        }
    
        @Setting(comment = "The behavior of the zoom key.\n\"HOLD\" needs the zoom key to be hold.\n\"TOGGLE\" has the zoom key toggle the zoom.\n\"PERSISTENT\" makes the zoom permanent.")
        public ZoomModes zoomMode = ZoomModes.HOLD;
    
        public enum ZoomModes {
            HOLD,
            TOGGLE,
            PERSISTENT
        }
    
        @Setting(comment = "Allows to increase or decrease zoom by scrolling.")
        public boolean zoomScrolling = true;

        @Setting(comment = "Adds zoom manipulation keys along with the zoom key.")
        public boolean extraKeybinds = true;

        @Setting(comment = "Adds an overlay in the screen during zoom. The overlay texture can be found at: assets/okzoomer/textures/misc/zoom_overlay.png")
        public boolean zoomOverlay = false;
    }

    @Group
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

        public double minimumZoomedFOV = 30.0;

        public double maximumZoomedFOV = 110.0;

        @Setting.Constrain.Range(min = 0.0)
        @Setting(comment = "The number which is decremented or incremented by zoom scrolling. Used when the zoom divisor is above the starting point.")
        public double scrollStep = 1.0;

        @Setting(comment = "The number which is decremented or incremented by zoom scrolling. Used when the zoom divisor is below the starting point.")
        public double lesserScrollStep = 0.5;

        @Setting.Constrain.Range(min = Double.MIN_NORMAL, max = 1.0)
        @Setting(comment = "The multiplier used for smooth transitions.")
        public double smoothMultiplier = 0.75;

        @Setting.Constrain.Range(min = Double.MIN_NORMAL)
        @Setting(comment = "The multiplier used for the multiplied cinematic camera.")
        public double cinematicMultiplier = 4.0;

        @Setting.Constrain.Range(min = 0)
        @Setting(comment = "The minimum value which the linear transition step can reach.")
        public double minimumLinearStep = 0.125;

        @Setting.Constrain.Range(min = Double.MIN_NORMAL)
        @Setting(comment = "The maximum value which the linear transition step can reach.")
        public double maximumLinearStep = 0.25;
    }

    @Group
    public static TweaksGroup tweaks = new TweaksGroup();

    public static class TweaksGroup {
        @Setting(comment = "Allows for resetting the zoom with the middle mouse button.")
        public boolean resetZoomWithMouse = true; 
        @Setting(comment = "If enabled, the \"Save Toolbar Activator\" keybind will be unbound if there's a conflict with the zoom key.")
        public boolean unbindConflictingKey = true;
        // TODO - Revert this once 5.0.0 is properly released
        @Setting(comment = "Prints a random owo in the console when the game starts. Enabled by default until full release.")
        public boolean printOwoOnStart = true;
        // @Setting(comment = "Hides the zoom overlay while the HUD's hidden.")
        // public boolean hideZoomOverlay = false;
    }
}
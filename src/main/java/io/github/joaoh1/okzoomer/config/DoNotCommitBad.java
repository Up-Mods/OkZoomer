package io.github.joaoh1.okzoomer.config;

//TODO - Get rid of this workaround after Fiber is fixed
public class DoNotCommitBad {
    private static String pastCinematicZoomValue = "off";
    private static boolean pastReduceSensitivityValue = true;
    private static boolean pastSmoothTransitionValue = true;
    private static boolean pastZoomToggleValue = false;
    private static boolean pastZoomScrollingValue = false;
    private static double pastZoomDivisorValue = 4.0D;
    
    public static final String getCinematicZoom() {
        String configValue = OkZoomerConfig.cinematicCamera.getValue();
        if (configValue != null) {
            pastCinematicZoomValue = configValue;
            return configValue;
        } else {
            return pastCinematicZoomValue;
        }
    }

    public static final boolean getReduceSensitivity() {
        Boolean configValue = OkZoomerConfig.reduceSensitivity.getValue();
        if (configValue != null) {
            pastReduceSensitivityValue = configValue;
            return configValue;
        } else {
            return pastReduceSensitivityValue;
        }
    }

    public static final boolean getSmoothTransition() {
        Boolean configValue = OkZoomerConfig.smoothTransition.getValue();
        if (configValue != null) {
            pastSmoothTransitionValue = configValue;
            return configValue;
        } else {
            return pastSmoothTransitionValue;
        }
    }

    public static final boolean getZoomToggle() {
        Boolean configValue = OkZoomerConfig.zoomToggle.getValue();
        if (configValue != null) {
            pastZoomToggleValue = configValue;
            return configValue;
        } else {
            return pastZoomToggleValue;
        }
    }

    public static final double getZoomDivisor() {
        Double configValue = OkZoomerConfig.zoomDivisor.getValue();
        if (configValue != null) {
            pastZoomDivisorValue = configValue;
            return configValue;
        } else {
            return pastZoomDivisorValue;
        }
    }

    public static final boolean getZoomScrolling() {
        Boolean configValue = OkZoomerConfig.zoomScrolling.getValue();
        if (configValue != null) {
            pastZoomScrollingValue = configValue;
            return configValue;
        } else {
            return pastZoomScrollingValue;
        }
    }
}
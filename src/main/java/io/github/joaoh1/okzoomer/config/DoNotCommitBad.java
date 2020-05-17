package io.github.joaoh1.okzoomer.config;

//TODO - Get rid of this workaround after Fiber is fixed
public class DoNotCommitBad {
    private static String pastCinematicZoomValue = "off";
    private static double pastCinematicMultiplierValue = 4.0D;
    private static boolean pastReduceSensitivityValue = true;
    private static boolean pastSmoothTransitionValue = true;
    private static String pastZoomTransitionValue = "smooth";
    private static boolean pastZoomToggleValue = false;
    private static String pastZoomScrollingValue = "by_step";
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

    public static final double getCinematicMultiplier() {
        Double configValue = OkZoomerConfig.cinematicMultiplier.getValue();
        if (configValue != null) {
            pastCinematicMultiplierValue = configValue;
            return configValue;
        } else {
            return pastCinematicMultiplierValue;
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

    /*
    public static final boolean getSmoothTransition() {
        Boolean configValue = OkZoomerConfig.smoothTransition.getValue();
        if (configValue != null) {
            pastSmoothTransitionValue = configValue;
            return configValue;
        } else {
            return pastSmoothTransitionValue;
        }
    }
    */

    public static final String getZoomTransition() {
        String configValue = OkZoomerConfig.zoomTransition.getValue();
        if (configValue != null) {
            pastZoomTransitionValue = configValue;
            return configValue;
        } else {
            return pastZoomTransitionValue;
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

    public static final String getZoomScrolling() {
        String configValue = OkZoomerConfig.zoomScrolling.getValue();
        if (configValue != null) {
            pastZoomScrollingValue = configValue;
            return configValue;
        } else {
            return pastZoomScrollingValue;
        }
    }
}
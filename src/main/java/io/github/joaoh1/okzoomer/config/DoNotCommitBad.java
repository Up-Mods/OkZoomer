package io.github.joaoh1.okzoomer.config;

//TODO - Get rid of this workaround after Fiber is fixed
public class DoNotCommitBad {
    public static final String getCinematicZoom() {
        String configValue = OkZoomerConfig.cinematicCamera.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return "off";
        }
    }

    public static final boolean getReduceSensitivity() {
        Boolean configValue = OkZoomerConfig.reduceSensitivity.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return false;
        }
    }

    public static final boolean getSmoothTransition() {
        Boolean configValue = OkZoomerConfig.smoothTransition.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return false;
        }
    }

    public static final boolean getZoomToggle() {
        Boolean configValue = OkZoomerConfig.zoomToggle.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return false;
        }
    }

    public static final double getZoomDivisor() {
        Double configValue = OkZoomerConfig.zoomDivisor.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return 4.0D;
        }
    }

    public static final boolean getZoomScrolling() {
        Boolean configValue = OkZoomerConfig.zoomScrolling.getValue();
        if (configValue != null) {
            return configValue;
        } else {
            return false;
        }
    }
}
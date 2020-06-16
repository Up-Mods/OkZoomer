package io.github.joaoh1.okzoomer.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Constrain;
import me.shedaniel.fiber2cloth.api.ClothSetting;
import me.shedaniel.fiber2cloth.api.ClothSetting.EnumHandler.EnumDisplayOption;

public class OkZoomerConfigPojo {
    @ClothSetting.Tooltip()
    @Setting.Constrain.Range(min = Double.MIN_NORMAL)
    @Setting(comment = "The divisor applied to the FOV when zooming.")
    public static double zoomDivisor = 4.0;

    public CinematicCameraOptions cinematicCamera = CinematicCameraOptions.off;

    enum CinematicCameraOptions {
        off,
        vanilla,
        multiplied
    }

    @Setting.Constrain.Range(min = Double.MIN_NORMAL)
    public double cinematicMultiplier = 4.0;

    public boolean reduceSensitivity = true;

    public ZoomTransitionOptions zoomTransition = ZoomTransitionOptions.smooth;

    enum ZoomTransitionOptions {
        off,
        smooth
    }

    public ZoomModes zoomMode = ZoomModes.hold;

    enum ZoomModes {
        hold,
        toggle,
        permanent
    }

    public boolean zoomScrolling = true;

    @ClothSetting.EnumHandler(EnumDisplayOption.BUTTON)
    @Setting.Constrain.Range(min = Double.MIN_NORMAL)
    public double minimumZoomDivisor = 1.0;

    @Setting.Constrain.Range(min = Double.MIN_NORMAL)
    public double maximumZoomDivisor = 50.0;
}
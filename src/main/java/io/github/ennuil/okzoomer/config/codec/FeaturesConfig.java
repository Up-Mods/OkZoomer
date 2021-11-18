package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.ennuil.okzoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomTransitionOptions;
import net.minecraft.util.StringIdentifiable;

public record FeaturesConfig(
    CinematicCameraOptions cinematicCamera,
    boolean reduceSensitivity,
    ZoomTransitionOptions zoomTransition,
    ZoomModes zoomMode,
    boolean zoomScrolling,
    boolean extraKeybinds,
    ZoomOverlays zoomOverlay
) {
    public static final Codec<FeaturesConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            StringIdentifiable.createCodec(
                CinematicCameraOptions::values,
                CinematicCameraOptions::valueOf
            ).fieldOf("cinematic_camera").forGetter(FeaturesConfig::cinematicCamera),
            Codec.BOOL.fieldOf("reduce_sensitivity").forGetter(FeaturesConfig::reduceSensitivity),
            StringIdentifiable.createCodec(
                ZoomTransitionOptions::values,
                ZoomTransitionOptions::valueOf
            ).fieldOf("zoom_transition").forGetter(FeaturesConfig::zoomTransition),
            StringIdentifiable.createCodec(
                ZoomModes::values,
                ZoomModes::valueOf
            ).fieldOf("zoom_mode").forGetter(FeaturesConfig::zoomMode),
            Codec.BOOL.fieldOf("zoom_scrolling").forGetter(FeaturesConfig::zoomScrolling),
            Codec.BOOL.fieldOf("extra_keybinds").forGetter(FeaturesConfig::extraKeybinds),
            StringIdentifiable.createCodec(
                ZoomOverlays::values,
                ZoomOverlays::valueOf
            ).fieldOf("zoom_overlay").forGetter(FeaturesConfig::zoomOverlay)
        )
        .apply(instance, FeaturesConfig::new)
    );

    public static FeaturesConfig getDefaultSettings() {
        return new FeaturesConfig(
            CinematicCameraOptions.OFF,
            true,
            ZoomTransitionOptions.SMOOTH,
            ZoomModes.HOLD,
            true,
            true,
            ZoomOverlays.OFF
        );
    }
}

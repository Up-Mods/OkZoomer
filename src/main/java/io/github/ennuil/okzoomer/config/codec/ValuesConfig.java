package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ValuesConfig(
    double zoomDivisor,
    double minimumZoomDivisor,
    double maximumZoomDivisor,
    double scrollStep,
    double lesserScrollStep,
    double smoothMultiplier,
    double cinematicMultiplier,
    double minimumLinearStep,
    double maximumLinearStep
) {
    public static final Codec<ValuesConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.doubleRange(Double.MIN_NORMAL, Double.MAX_VALUE).fieldOf("zoom_divisor").orElse(4.0).forGetter(ValuesConfig::zoomDivisor),
            Codec.doubleRange(Double.MIN_NORMAL, Double.MAX_VALUE).fieldOf("minimum_zoom_divisor").orElse(1.0).forGetter(ValuesConfig::minimumZoomDivisor),
            Codec.doubleRange(Double.MIN_NORMAL, Double.MAX_VALUE).fieldOf("maximum_zoom_divisor").orElse(50.0).forGetter(ValuesConfig::maximumZoomDivisor),
            Codec.doubleRange(0.0, Double.MAX_VALUE).fieldOf("scroll_step").orElse(1.0).forGetter(ValuesConfig::scrollStep),
            Codec.DOUBLE.fieldOf("lesser_scroll_step").orElse(0.5).forGetter(ValuesConfig::lesserScrollStep),
            Codec.doubleRange(Double.MIN_NORMAL, 1.0).fieldOf("smooth_multiplier").orElse(0.75).forGetter(ValuesConfig::smoothMultiplier),
            Codec.doubleRange(Double.MIN_NORMAL, 4.0).fieldOf("cinematic_multiplier").orElse(4.0).forGetter(ValuesConfig::cinematicMultiplier),
            Codec.doubleRange(0.0, Double.MAX_VALUE).fieldOf("minimum_linear_step").orElse(0.125).forGetter(ValuesConfig::minimumLinearStep),
            Codec.doubleRange(Double.MIN_NORMAL, Double.MAX_VALUE).fieldOf("maximum_linear_step").orElse(0.25).forGetter(ValuesConfig::maximumLinearStep)
        )
        .apply(instance, ValuesConfig::new)
    );

    public static ValuesConfig getDefaultSettings() {
        return new ValuesConfig(
            4.0,
            1.0,
            50.0,
            1.0,
            0.5,
            0.75,
            4.0,
            0.125,
            0.25
        );
    }
}

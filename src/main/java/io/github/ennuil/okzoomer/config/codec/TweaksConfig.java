package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TweaksConfig(
    boolean resetZoomWithMouse,
    boolean unbindConflictingKey,
    boolean useSpyglassTexture,
    boolean useSpyglassSounds,
    boolean showRestrictionToasts,
    boolean printOwoOnStart
) {
    public static final Codec<TweaksConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.BOOL.fieldOf("reset_zoom_with_mouse").orElse(true).forGetter(TweaksConfig::resetZoomWithMouse),
            Codec.BOOL.fieldOf("unbind_conflicting_key").orElse(true).forGetter(TweaksConfig::unbindConflictingKey),
            Codec.BOOL.fieldOf("use_spyglass_texture").orElse(false).forGetter(TweaksConfig::useSpyglassTexture),
            Codec.BOOL.fieldOf("use_spyglass_sounds").orElse(false).forGetter(TweaksConfig::useSpyglassSounds),
            Codec.BOOL.fieldOf("show_restriction_toasts").orElse(true).forGetter(TweaksConfig::showRestrictionToasts),
            Codec.BOOL.fieldOf("print_owo_on_start").orElse(true).forGetter(TweaksConfig::printOwoOnStart)
        )
        .apply(instance, TweaksConfig::new)
    );

    public static TweaksConfig getDefaultSettings() {
        return new TweaksConfig(
            true,
            true,
            false,
            false,
            true,
            true
        );
    }
}

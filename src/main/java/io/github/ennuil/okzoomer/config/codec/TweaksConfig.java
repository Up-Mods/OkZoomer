package io.github.ennuil.okzoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TweaksConfig(
    boolean resetZoomWithMouse,
    boolean unbindConflictingKey,
    boolean useSpyglassTexture,
    boolean useSpyglassSounds,
    boolean printOwoOnStart
) {
    public static final Codec<TweaksConfig> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.BOOL.fieldOf("reset_zoom_with_mouse").forGetter(TweaksConfig::resetZoomWithMouse),
            Codec.BOOL.fieldOf("unbind_conflicting_key").forGetter(TweaksConfig::unbindConflictingKey),
            Codec.BOOL.fieldOf("use_spyglass_texture").forGetter(TweaksConfig::useSpyglassTexture),
            Codec.BOOL.fieldOf("use_spyglass_sounds").forGetter(TweaksConfig::useSpyglassSounds),
            Codec.BOOL.fieldOf("print_owo_on_start").forGetter(TweaksConfig::printOwoOnStart)
        )
        .apply(instance, TweaksConfig::new)
    );

    public static TweaksConfig getDefaultSettings() {
        return new TweaksConfig(
            true,
            true,
            false,
            false,
            true
        );
    }

    public static TweaksConfig getDefaultSettingsWithUnboundKey() {
        return new TweaksConfig(
            true,
            true,
            false,
            false,
            true
        );
    }
}

package io.github.ennuil.ok_zoomer.config.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record OkZoomerConfig(
	FeaturesConfig features,
	ValuesConfig values,
	TweaksConfig tweaks
) {
	public static final Codec<OkZoomerConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			FeaturesConfig.CODEC.fieldOf("features").forGetter(OkZoomerConfig::features),
			ValuesConfig.CODEC.fieldOf("values").forGetter(OkZoomerConfig::values),
			TweaksConfig.CODEC.fieldOf("tweaks").forGetter(OkZoomerConfig::tweaks)
		)
		.apply(instance, OkZoomerConfig::new)
	);

	public static OkZoomerConfig getDefaultSettings() {
		return new OkZoomerConfig(
			new FeaturesConfig(),
			new ValuesConfig(),
			new TweaksConfig()
		);
	}
}

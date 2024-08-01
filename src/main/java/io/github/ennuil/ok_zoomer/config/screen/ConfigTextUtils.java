package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingScheme;
import org.quiltmc.config.api.values.TrackedValue;

import java.util.Locale;

public class ConfigTextUtils {
	private final Config config;
	private final NamingScheme scheme;

	public ConfigTextUtils(Config config) {
		this.config = config;
		this.scheme = this.config.metadata(SerializedNameConvention.TYPE);
	}

    public static Component getConfigTitle(ResourceLocation configId) {
        return Component.translatable("config." + configId.getNamespace() + ".title");
    }

	public Component getCategoryText(String category) {
		return Component.translatable("config." + this.config.family() + "." + this.scheme.coerce(category));
	}

	public Component getOptionText(TrackedValue<?> trackedValue) {
		return Component.translatable(String.format("config.%s.%s", this.config.family(), this.scheme.coerce(trackedValue.key().toString())));
	}

	public Component getOptionTextTooltip(TrackedValue<?> trackedValue) {
		return Component.translatable(String.format("config.%s.%s.tooltip", this.config.family(), this.scheme.coerce(trackedValue.key().toString())));
	}

	public Component getEnumOptionText(TrackedValue<?> trackedValue, ConfigEnums.ConfigEnum configEnum) {
		return Component.translatable(String.format("config.%s.%s.%s", this.config.family(), this.scheme.coerce(trackedValue.key().toString()), configEnum.toString().toLowerCase(Locale.ROOT)));
	}

	public Component getEnumOptionTextTooltip(TrackedValue<?> trackedValue, ConfigEnums.ConfigEnum configEnum) {
		return Component.translatable(String.format("config.%s.%s.%s.tooltip", this.config.family(), this.scheme.coerce(trackedValue.key().toString()), configEnum.toString().toLowerCase(Locale.ROOT)));
	}
}

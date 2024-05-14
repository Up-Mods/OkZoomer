package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingScheme;
import org.quiltmc.config.api.values.TrackedValue;

public class ConfigTextUtils {
	private final Config config;
	private final NamingScheme scheme;

	public ConfigTextUtils(Config config) {
		this.config = config;
		this.scheme = this.config.metadata(SerializedNameConvention.TYPE);
	}

    public static Text getConfigTitle(Identifier configId) {
        return Text.translatable("config." + configId.getNamespace() + ".title");
    }

	public Text getCategoryText(String category) {
		return Text.translatable("config." + this.config.family() + "." + this.scheme.coerce(category));
	}

	public Text getOptionText(TrackedValue<?> trackedValue) {
		return Text.translatable(String.format("config.%s.%s", this.config.family(), this.scheme.coerce(trackedValue.key().toString())));
	}

	public Text getOptionTextTooltip(TrackedValue<?> trackedValue) {
		return Text.translatable(String.format("config.%s.%s.tooltip", this.config.family(), this.scheme.coerce(trackedValue.key().toString())));
	}

	public Text getEnumOptionText(TrackedValue<?> trackedValue, ConfigEnums.ConfigEnum configEnum) {
		return Text.translatable(String.format("config.%s.%s.%s", this.config.family(), this.scheme.coerce(trackedValue.key().toString()), configEnum.toString().toLowerCase()));
	}

	public Text getEnumOptionTextTooltip(TrackedValue<?> trackedValue, ConfigEnums.ConfigEnum configEnum) {
		return Text.translatable(String.format("config.%s.%s.%s.tooltip", this.config.family(), this.scheme.coerce(trackedValue.key().toString()), configEnum.toString().toLowerCase()));
	}
}

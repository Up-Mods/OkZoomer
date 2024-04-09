package io.github.ennuil.ok_zoomer.config.screen.v2;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigTextUtils {
    public static Text getConfigTitle(Identifier configId) {
        return Text.translatable("config." + configId.getNamespace() + ".title");
    }

	public static Text getCategoryText(Identifier configId, String category) {
		return Text.translatable("config." + configId.getNamespace() + "." + category);
	}
}

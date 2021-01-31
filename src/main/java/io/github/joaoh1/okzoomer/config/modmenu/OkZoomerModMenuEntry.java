package io.github.joaoh1.okzoomer.config.modmenu;

import io.github.joaoh1.okzoomer.config.OkZoomerConfigScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class OkZoomerModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> OkZoomerConfigScreen.getConfigScreen(screen);
	}
}
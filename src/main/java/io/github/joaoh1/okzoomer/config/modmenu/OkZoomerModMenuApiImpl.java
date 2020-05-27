package io.github.joaoh1.okzoomer.config.modmenu;

import io.github.joaoh1.okzoomer.config.OkZoomerConfigScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class OkZoomerModMenuApiImpl implements ModMenuApi {
	@Override
	public String getModId() {
		return "okzoomer";
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> OkZoomerConfigScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen);
	}
}
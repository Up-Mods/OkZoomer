package io.github.joaoh1.okzoomer.client.config.modmenu;

import java.util.function.Function;

import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigScreen;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class OkZoomerModMenuEntry implements ModMenuApi {
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> OkZoomerConfigScreen.getConfigScreen(screen);
	}

	@Override
	public String getModId() {
		return "okzoomer";
	}
}
package io.github.ennuil.ok_zoomer.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class OkZoomerModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return OkZoomerConfigScreen::new;
	}
}

package page.langeweile.ok_zoomer.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;

public class OkZoomerModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return OkZoomerConfigScreen::new;
	}
}

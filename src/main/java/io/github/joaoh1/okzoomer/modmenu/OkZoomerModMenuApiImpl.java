package io.github.joaoh1.okzoomer.modmenu;

import io.github.joaoh1.okzoomer.OkZoomerConfig;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class OkZoomerModMenuApiImpl implements ModMenuApi {
    @Override
    public String getModId() {
        return "okzoomer";
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> AutoConfig.getConfigScreen(OkZoomerConfig.class, screen).get();
	}
}
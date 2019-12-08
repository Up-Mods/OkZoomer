package io.github.joaoh1.okzoomer.modmenu;

import com.google.common.base.Function;
import io.github.joaoh1.okzoomer.OkZoomerConfig;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class OkZoomerModMenuApiImpl implements ModMenuApi {
    @Override
    public String getModId() {
        return "okzoomer";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(OkZoomerConfig.class, screen).get();
    }
}
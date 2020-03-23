package io.github.joaoh1.okzoomer;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        //Register the config before anything else uses it, this is a temporary fix to something that will probably require a rewrite of this mod.
        AutoConfig.register(OkZoomerConfig.class, JanksonConfigSerializer::new);
    }
}
package io.github.ennuil.okzoomer;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MixinExtrasInitializer implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }
}

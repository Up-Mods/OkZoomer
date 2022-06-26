package io.github.ennuil.ok_zoomer;

import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessors;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;

import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch(ModContainer mod) {
		MixinExtrasBootstrap.init();
		ConfigFieldAnnotationProcessors.register(WidgetSize.class, new WidgetSize.Processor());
	}
}

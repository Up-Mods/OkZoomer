package io.github.ennuil.ok_zoomer;

import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessors;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

import io.github.ennuil.ok_zoomer.config.WidgetSize;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch(ModContainer mod) {
		ConfigFieldAnnotationProcessors.register(WidgetSize.class, new WidgetSize.Processor());
	}
}

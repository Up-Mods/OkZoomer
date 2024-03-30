package io.github.ennuil.ok_zoomer;

import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch(ModContainer mod) {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
	}
}

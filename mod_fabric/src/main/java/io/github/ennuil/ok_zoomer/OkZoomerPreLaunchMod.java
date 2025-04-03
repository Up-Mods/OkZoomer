package io.github.ennuil.ok_zoomer;

import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
	}
}

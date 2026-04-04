package page.langeweile.ok_zoomer.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import page.langeweile.ok_zoomer.config.metadata.RangeSubset;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize;

public class OkZoomerPreLaunchMod implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
		ConfigFieldAnnotationProcessor.register(RangeSubset.class, new RangeSubset.Processor());
	}
}

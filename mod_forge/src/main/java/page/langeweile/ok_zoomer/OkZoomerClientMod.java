package page.langeweile.ok_zoomer;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.config.metadata.RangeSubset;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;
import page.langeweile.ok_zoomer.events.ApplyLoadOnceOptionsEvent;
import page.langeweile.ok_zoomer.utils.ForgeZoomUtils;

@Mod(value = "ok_zoomer")
public class OkZoomerClientMod {
	public OkZoomerClientMod() {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
		ConfigFieldAnnotationProcessor.register(RangeSubset.class, new RangeSubset.Processor());
		OkZoomerConfigManager.init();

		ApplyLoadOnceOptionsEvent.readyClient();

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
			(mod2, screen) -> new OkZoomerConfigScreen(screen)
		));

		ForgeZoomUtils.defineSafeSmartOcclusion();
		ForgeZoomUtils.addInitialPredicates();
	}
}

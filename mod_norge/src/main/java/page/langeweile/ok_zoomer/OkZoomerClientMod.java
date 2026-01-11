package page.langeweile.ok_zoomer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.config.metadata.RangeSubset;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;
import page.langeweile.ok_zoomer.utils.NorgeZoomUtils;

@Mod(value = "ok_zoomer", dist = Dist.CLIENT)
public class OkZoomerClientMod {
	public OkZoomerClientMod(IEventBus bus, ModContainer mod) {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
		ConfigFieldAnnotationProcessor.register(RangeSubset.class, new RangeSubset.Processor());

		OkZoomerConfigManager.init();

		mod.registerExtensionPoint(IConfigScreenFactory.class, ((mod2, screen) -> new OkZoomerConfigScreen(screen)));

		NorgeZoomUtils.defineSafeSmartOcclusion();
		NorgeZoomUtils.addInitialPredicates();
	}
}

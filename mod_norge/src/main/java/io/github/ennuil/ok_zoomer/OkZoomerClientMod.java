package io.github.ennuil.ok_zoomer;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.sound.NorgeSoundEvents;
import io.github.ennuil.ok_zoomer.utils.NorgeZoomUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;

@Mod(value = "ok_zoomer", dist = Dist.CLIENT)
public class OkZoomerClientMod {
	public OkZoomerClientMod(IEventBus bus, ModContainer mod) {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
		OkZoomerConfigManager.init();
		NorgeSoundEvents.SOUND_EVENTS.register(bus);

		mod.registerExtensionPoint(IConfigScreenFactory.class, ((mod2, screen) -> new OkZoomerConfigScreen(screen)));

		NorgeZoomUtils.defineSafeSmartOcclusion();
		NorgeZoomUtils.addInitialPredicates();
	}
}

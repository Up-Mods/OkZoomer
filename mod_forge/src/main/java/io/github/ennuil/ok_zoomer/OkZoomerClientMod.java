package io.github.ennuil.ok_zoomer;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.events.ApplyLoadOnceOptionsEvent;
import io.github.ennuil.ok_zoomer.utils.ForgeZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;

@Mod(value = "ok_zoomer")
public class OkZoomerClientMod {
	public OkZoomerClientMod() {
		ConfigFieldAnnotationProcessor.register(WidgetSize.class, new WidgetSize.Processor());
		OkZoomerConfigManager.init();

		ApplyLoadOnceOptionsEvent.readyClient(Minecraft.getInstance());

		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
			(mod2, screen) -> new OkZoomerConfigScreen(screen)
		));

		ForgeZoomUtils.defineSafeSmartOcclusion();
		ForgeZoomUtils.addInitialPredicates();
	}
}

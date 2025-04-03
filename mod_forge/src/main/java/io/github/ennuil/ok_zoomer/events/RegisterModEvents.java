package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = "ok_zoomer")
public class RegisterModEvents {
	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		ZoomKeyBinds.ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		event.register(ZoomKeyBinds.ZOOM_KEY);
		if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
			ZoomKeyBinds.DECREASE_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			ZoomKeyBinds.INCREASE_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			ZoomKeyBinds.RESET_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			event.register(ZoomKeyBinds.DECREASE_ZOOM_KEY);
			event.register(ZoomKeyBinds.INCREASE_ZOOM_KEY);
			event.register(ZoomKeyBinds.RESET_ZOOM_KEY);
		}
	}

	@SubscribeEvent
	public static void registerOverlay(RegisterGuiOverlaysEvent event) {
		event.registerBelow(VanillaGuiOverlay.SPYGLASS.id(), "ok_zoomer_overlay", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
			if (Zoom.getZoomOverlay() != null) {
				var overlay = Zoom.getZoomOverlay();
				overlay.tickBeforeRender();
				if (overlay.getActive()) {
					// We don't cancel overlays because uhhhh, Forge breaks Vanilla behavior and doesn't do that
					gui.setupOverlayRenderState(true, false);
					overlay.renderOverlay(graphics, Zoom.getTransitionMode());
				}
			}
		});
	}
}

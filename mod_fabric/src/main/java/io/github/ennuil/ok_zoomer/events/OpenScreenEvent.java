package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.utils.FabricZoomUtils;
import net.minecraft.client.Minecraft;

public class OpenScreenEvent {
	public static void endClientTick(Minecraft minecraft) {
		if (FabricZoomUtils.shouldOpenCommandScreen()) {
			minecraft.setScreen(new OkZoomerConfigScreen(minecraft.screen));
			FabricZoomUtils.setOpenCommandScreen(false);
		}
	}
}

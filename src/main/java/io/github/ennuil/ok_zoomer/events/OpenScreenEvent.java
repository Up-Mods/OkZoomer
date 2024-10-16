package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;

public class OpenScreenEvent {
	public static void endClientTick(Minecraft minecraft) {
		if (ZoomUtils.shouldOpenCommandScreen()) {
			minecraft.setScreen(new OkZoomerConfigScreen(minecraft.screen));
			ZoomUtils.setOpenCommandScreen(false);
		}
	}
}

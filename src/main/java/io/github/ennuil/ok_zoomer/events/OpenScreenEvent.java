package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.commands.OkZoomerCommandScreen;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;

public class OpenScreenEvent {
	public static void endClientTick(Minecraft minecraft) {
		if (ZoomUtils.shouldOpenCommandScreen()) {
			minecraft.setScreen(new OkZoomerCommandScreen());
			ZoomUtils.setOpenCommandScreen(false);
		}
	}
}

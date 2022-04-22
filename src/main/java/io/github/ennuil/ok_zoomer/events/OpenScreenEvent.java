package io.github.ennuil.ok_zoomer.events;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import io.github.ennuil.ok_zoomer.commands.OkZoomerCommandScreen;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;

public class OpenScreenEvent implements ClientTickEvents.End {
	@Override
	public void endClientTick(MinecraftClient client) {
		if (ZoomUtils.shouldOpenCommandScreen()) {
			client.setScreen(new OkZoomerCommandScreen());
			ZoomUtils.setOpenCommandScreen(false);
		}
	}
}

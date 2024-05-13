package io.github.ennuil.ok_zoomer.events;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.OwoUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class ApplyLoadOnceOptionsEvent implements ClientLifecycleEvents.Ready {
	@Override
	public void readyClient(MinecraftClient client) {
		// uwu
		if (OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart.value()) {
			OwoUtils.printOwo();
		}

		// This handles the unbinding of the "Save Toolbar Activator" key
		if (OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.value()) {
			ZoomUtils.unbindConflictingKey(client, false);
			OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.setValue(false, true);
		}
	}
}

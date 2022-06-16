package io.github.ennuil.ok_zoomer.events;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.OwoUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class LoadConfigEvent implements ClientLifecycleEvents.Ready {
	@Override
	public void readyClient(MinecraftClient client) {
		// uwu
		if (OkZoomerConfigManager.PRINT_OWO_ON_START.value()) {
			OwoUtils.printOwo();
		}

		// This handles the unbinding of the "Save Toolbar Activator" key
		if (OkZoomerConfigManager.UNBIND_CONFLICTING_KEY.value()) {
			ZoomUtils.unbindConflictingKey(client, false);
			OkZoomerConfigManager.UNBIND_CONFLICTING_KEY.setValue(false, true);
		}
	}
}

package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.OwoUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class ApplyLoadOnceOptionsEvent {
	public static void readyClient(Minecraft minecraft) {
		// uwu
		if (OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart.value()) {
			OwoUtils.printOwo();
		}

		// This handles the unbinding of the "Save Toolbar Activator" key
		if (OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.value()) {
			ZoomUtils.unbindConflictingKey(minecraft, false);
			OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.setValue(false, true);
		}
	}
}

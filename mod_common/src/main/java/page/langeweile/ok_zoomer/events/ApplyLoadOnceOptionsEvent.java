package page.langeweile.ok_zoomer.events;

import net.minecraft.client.Minecraft;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.OwoUtils;
import page.langeweile.ok_zoomer.utils.ZoomUtils;

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

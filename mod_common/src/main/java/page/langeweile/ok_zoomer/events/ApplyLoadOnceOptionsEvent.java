package page.langeweile.ok_zoomer.events;

import net.minecraft.client.Minecraft;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.OwoUtils;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class ApplyLoadOnceOptionsEvent {
	public static void readyClient() {
		// uwu
		if (OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart.value()) {
			OwoUtils.printOwo();
		}
	}
}

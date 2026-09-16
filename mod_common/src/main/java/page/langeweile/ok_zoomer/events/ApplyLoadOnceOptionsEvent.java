package page.langeweile.ok_zoomer.events;

import org.quiltmc.config.api.values.TrackedValue;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.OwoUtils;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class ApplyLoadOnceOptionsEvent {
	public static void readyClient() {
		// uwu
		if (OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart.value()) {
			OwoUtils.printOwo();
		}

		// Migrate See Distant Entities so we don't crash
		TrackedValue<?> seeDistantEntities = OkZoomerConfigManager.CONFIG.appearance.seeDistantEntities;
		OkZoomerConfigManager.CONFIG.appearance.seeDistantEntities.setValue(
			switch (seeDistantEntities.value()) {
				case String oldV -> !oldV.equals("OFF");
				case Boolean newV -> newV;
				default -> true;
			}
		);
	}
}

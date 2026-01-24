package page.langeweile.ok_zoomer.events;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.key_binds.ZoomKeyBinds;import page.langeweile.ok_zoomer.utils.OwoUtils;
import page.langeweile.ok_zoomer.utils.ZoomUtils;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file
public class ApplyLoadOnceOptionsEvent {
	public static void readyClient() {
		// uwu
		if (OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart.value()) {
			OwoUtils.printOwo();
		}
	}

	// Made Fabric-exclusive since (Neo)Forge doesn't have this problem
	public static void readyClientFabric(Minecraft minecraft) {
		// This handles the unbinding of the "Save Toolbar Activator" key
		if (OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.value()) {
			if (ZoomKeyBinds.ZOOM_KEY.isDefault()) {
				if (minecraft.options.keySaveHotbarActivator.isDefault()) {
					ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated until set manually in the config file.");
					minecraft.options.keySaveHotbarActivator.setKey(InputConstants.UNKNOWN);
					minecraft.options.save();
					KeyMapping.resetMapping();
				} else {
					ZoomUtils.LOGGER.info("[Ok Zoomer] No conflicts with the \"Save Toolbar Activator\" keybind were found!");
				}
			}

			OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey.setValue(false, true);
		}
	}
}

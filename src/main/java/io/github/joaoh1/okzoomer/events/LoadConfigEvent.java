package io.github.joaoh1.okzoomer.events;

import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.utils.OwoUtils;
import io.github.joaoh1.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

//The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file.
public class LoadConfigEvent {
    public static void registerEvent() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			//Attempt to load the config if it hasn't been loaded yet, which is unlikely due to extra keybinds.
			if (!OkZoomerConfig.isConfigLoaded) {
				OkZoomerConfig.loadModConfig();
			}
			
			//uwu
			OwoUtils.printOwo();

			//This handles the hijacking of the "Save Toolbar Activator" key.
			if (OkZoomerConfigPojo.tweaks.unbindConflictingKey) {
				ZoomUtils.unbindConflictingKey(client, false);
				OkZoomerConfigPojo.tweaks.unbindConflictingKey = false;
				OkZoomerConfig.saveModConfig();
			}
		});
    }
}
package io.github.ennuil.okzoomer.events;

import io.github.ennuil.okzoomer.config.OkZoomerConfig;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo;
import io.github.ennuil.okzoomer.utils.OwoUtils;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

// The event that makes sure to load the config and puts any load-once options in effect if enabled through the config file.
public class LoadConfigEvent {
    public static void registerEvent() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            // Attempt to load the config if it hasn't been loaded yet, which is unlikely due to extra keybinds.
            if (!OkZoomerConfig.isConfigLoaded) {
                OkZoomerConfig.loadModConfig();
            }
            
            // uwu
            if (OkZoomerConfigPojo.tweaks.printOwoOnStart) {
                OwoUtils.printOwo();
            }

            // This handles the unbinding of the "Save Toolbar Activator" key.
            if (OkZoomerConfigPojo.tweaks.unbindConflictingKey) {
                ZoomUtils.unbindConflictingKey(client, false);
                OkZoomerConfigPojo.tweaks.unbindConflictingKey = false;
                OkZoomerConfig.saveModConfig();
            }
        });
    }
}
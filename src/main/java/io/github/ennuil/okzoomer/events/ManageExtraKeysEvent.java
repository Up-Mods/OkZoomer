package io.github.ennuil.okzoomer.events;

import io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

// This event manages the extra keybinds' behavior
public class ManageExtraKeysEvent {
    public static void registerEvent() {
        // Register the event only if the "Extra Keybinds" option is enabled
        if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (ZoomPackets.getDisableZoomScrolling()) return;
    
                if (ZoomKeyBinds.DECREASE_ZOOM_KEY.isPressed() && !ZoomKeyBinds.INCREASE_ZOOM_KEY.isPressed()) {
                    ZoomUtils.changeZoomDivisor(false);
                }
    
                if (ZoomKeyBinds.INCREASE_ZOOM_KEY.isPressed() && !ZoomKeyBinds.DECREASE_ZOOM_KEY.isPressed()) {
                    ZoomUtils.changeZoomDivisor(true);
                }
    
                if (ZoomKeyBinds.RESET_ZOOM_KEY.isPressed()) {
                    ZoomUtils.resetZoomDivisor();
                }
            });
        }
    }
}
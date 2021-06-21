package io.github.ennuil.okzoomer.events;

import io.github.ennuil.okzoomer.keybinds.ZoomKeybinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

// This event manages the extra keybinds' behavior.
public class ManageExtraKeysEvent {
    public static void registerEvent() {
        // Register the event only if the "Extra Keybinds" option is enabled.
        if (ZoomKeybinds.areExtraKeybindsEnabled()) {
			ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (ZoomPackets.getDisableZoomScrolling()) return;
    
                if (ZoomKeybinds.decreaseZoomKey.isPressed()) {
                    ZoomUtils.changeZoomDivisor(false);
                }
    
                if (ZoomKeybinds.increaseZoomKey.isPressed()) {
                    ZoomUtils.changeZoomDivisor(true);
                }
    
                if (ZoomKeybinds.resetZoomKey.isPressed()) {
                    ZoomUtils.resetZoomDivisor();
                }
            });
		}
    }
}
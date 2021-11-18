package io.github.ennuil.okzoomer.events;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.keybinds.ZoomKeybinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.sound.SoundEvents;

// This event is responsible for managing the zoom signal.
public class ManageZoomEvent {
    // Used internally in order to make zoom toggling possible.
    private static boolean lastZoomPress = false;

    // Used internally in order to make persistent zoom less buggy.
    private static boolean persistentZoom = false;
    
    public static void registerEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // If zoom is disabled, do not allow for zooming at all.
            if (ZoomPackets.getDisableZoom()) return;

            // Handle zoom mode changes.
            if (!OkZoomerConfigManager.INSTANCE.features().zoomMode().equals(ZoomModes.HOLD)) {
                if (!persistentZoom) {
                    persistentZoom = true;
                    lastZoomPress = true;
                    ZoomUtils.zoomerZoom.resetZoomDivisor();
                }
            } else {
                if (persistentZoom) {
                    persistentZoom = false;
                    lastZoomPress = true;
                }
            }

            // If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable and the zoom divisor adjustable.
            if (ZoomKeybinds.zoomKey.isPressed() == lastZoomPress) return;

            switch (OkZoomerConfigManager.INSTANCE.features().zoomMode()) {
                case HOLD -> {
                    // If the zoom needs to be held, then the zoom signal is determined by if the key is pressed or not.
                    ZoomUtils.zoomerZoom.setZoom(ZoomKeybinds.zoomKey.isPressed());
                    ZoomUtils.zoomerZoom.resetZoomDivisor();
                }
                case TOGGLE -> {
                    // If the zoom needs to be toggled, toggle the zoom signal instead.
                    if (ZoomKeybinds.zoomKey.isPressed()) {
                        ZoomUtils.zoomerZoom.setZoom(!ZoomUtils.zoomerZoom.getZoom());
                        ZoomUtils.zoomerZoom.resetZoomDivisor();
                    }
                }
                case PERSISTENT -> {
                    // If persistent zoom is enabled, just keep the zoom on.
                    ZoomUtils.zoomerZoom.setZoom(true);
                }
            }

            // FIXME - Huh, apparently it happens multiple times with other zoom modes. Needs fix
            if (client.player != null && OkZoomerConfigManager.INSTANCE.tweaks().useSpyglassSounds()) {
                client.player.playSound(ZoomUtils.zoomerZoom.getZoom() ? SoundEvents.ITEM_SPYGLASS_USE : SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
            }

            // Set the previous zoom signal for the next tick.
            lastZoomPress = ZoomKeybinds.zoomKey.isPressed();
        });
    }
}
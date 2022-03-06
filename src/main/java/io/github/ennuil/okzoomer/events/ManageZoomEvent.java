package io.github.ennuil.okzoomer.events;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.sound.SoundEvents;

// This event is responsible for managing the zoom signal.
public class ManageZoomEvent {
    // Used internally in order to make zoom toggling possible
    private static boolean lastZoomPress = false;

    // Used internally in order to make persistent zoom less buggy
    private static boolean persistentZoom = false;

    // Used internally in order to avoid sound problems
    private static boolean doSpyglassSound = OkZoomerConfigManager.configInstance.tweaks().getUseSpyglassSounds();
    
    public static void registerEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // We need the player for spyglass shenanigans
            if (client.player == null) return;
            
            // If zoom is disabled, do not allow for zooming at all
            if (ZoomPackets.getDisableZoom()) return;

            if (switch (OkZoomerConfigManager.configInstance.features().getSpyglassDependency()) {
                case REQUIRE_ITEM -> true;
                case BOTH -> true;
                default -> false;
            }) {
                if (!client.player.getInventory().method_7382(ZoomUtils.ZOOM_DEPENDENCIES_TAG)) {
                    ZoomUtils.ZOOMER_ZOOM.setZoom(false);
                    ZoomUtils.resetZoomDivisor(false);
                    return;
                }
            }

            // Handle zoom mode changes.
            if (!OkZoomerConfigManager.configInstance.features().getZoomMode().equals(ZoomModes.HOLD)) {
                if (!persistentZoom) {
                    persistentZoom = true;
                    lastZoomPress = true;
                    ZoomUtils.ZOOMER_ZOOM.resetZoomDivisor();
                }
            } else {
                if (persistentZoom) {
                    persistentZoom = false;
                    lastZoomPress = true;
                }
            }

            // TODO - describe
            boolean isUsingSpyglass = switch (OkZoomerConfigManager.configInstance.features().getSpyglassDependency()) {
                case REPLACE_ZOOM -> true;
                case BOTH -> true;
                default -> false;
            };

            boolean zoomPress = ZoomKeyBinds.ZOOM_KEY.isPressed();
            if (!zoomPress && isUsingSpyglass) {
                zoomPress = client.player.isUsingSpyglass();
            }

            // If the press state is the same as the previous tick's, cancel the rest
            // This makes toggling usable and the zoom divisor adjustable
            if (zoomPress == lastZoomPress) return;

            doSpyglassSound = OkZoomerConfigManager.configInstance.tweaks().getUseSpyglassSounds();

            switch (OkZoomerConfigManager.configInstance.features().getZoomMode()) {
                case HOLD -> {
                    // If the zoom needs to be held, then the zoom signal is determined by if the key is pressed or not
                    ZoomUtils.ZOOMER_ZOOM.setZoom(zoomPress);
                    ZoomUtils.resetZoomDivisor(false);
                }
                case TOGGLE -> {
                    // If the zoom needs to be toggled, toggle the zoom signal instead
                    if (zoomPress) {
                        ZoomUtils.ZOOMER_ZOOM.setZoom(!ZoomUtils.ZOOMER_ZOOM.getZoom());
                        ZoomUtils.resetZoomDivisor(false);
                    } else {
                        doSpyglassSound = false;
                    }
                }
                case PERSISTENT -> {
                    // If persistent zoom is enabled, just keep the zoom on
                    ZoomUtils.ZOOMER_ZOOM.setZoom(true);
                    ZoomUtils.keepZoomStepsWithinBounds();
                }
            }

            // TODO - Have this be key press-only; The spyglass already makes sounds
            if (client.player != null && doSpyglassSound) {
                boolean soundDirection = !OkZoomerConfigManager.configInstance.features().getZoomMode().equals(ZoomModes.PERSISTENT)
                    ? ZoomUtils.ZOOMER_ZOOM.getZoom()
                    : zoomPress;

                client.player.playSound(soundDirection ? SoundEvents.ITEM_SPYGLASS_USE : SoundEvents.ITEM_SPYGLASS_STOP_USING, 1.0F, 1.0F);
            }

            // Set the previous zoom signal for the next tick
            lastZoomPress = zoomPress;
        });
    }
}
package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

// This event is responsible for managing the zoom signal.
public class ManageZoomEvent implements ClientTickEvents.Start {
	// Used internally in order to make zoom toggling possible
	private static boolean lastZooming = false;

	// Used internally in order to make persistent zoom less buggy
	private static boolean persistentZoom = false;

	@Override
	public void startClientTick(Minecraft minecraft) {
		// We need the player for spyglass shenanigans
		if (minecraft.player == null) return;

		// If zoom is disabled, do not allow for zooming at all
		boolean disableZoom = ZoomPackets.shouldDisableZoom() ||
			(switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
				case REQUIRE_ITEM, BOTH -> true;
				default -> false;
			} && !minecraft.player.getInventory().contains(ZoomUtils.ZOOM_DEPENDENCIES_TAG));

		if (disableZoom) {
			ZoomUtils.ZOOMER_ZOOM.setZoom(false);
			ZoomUtils.resetZoomDivisor(false);
			lastZooming = false;
			return;
		}

		// Handle zoom mode changes.
		if (!OkZoomerConfigManager.CONFIG.features.zoomMode.value().equals(ZoomModes.HOLD)) {
			if (!persistentZoom) {
				persistentZoom = true;
				lastZooming = true;
				ZoomUtils.resetZoomDivisor(false);
			}
		} else {
			if (persistentZoom) {
				persistentZoom = false;
				lastZooming = true;
			}
		}

		// Gathers all variables about if the press was with zoom key or with the spyglass
		boolean isUsingSpyglass = switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		};
		boolean keyPress = ZoomKeyBinds.ZOOM_KEY.isDown();
		boolean spyglassUse = minecraft.player.isScoping();
		boolean zooming = keyPress || (isUsingSpyglass && spyglassUse);

		// If the press state is the same as the previous tick's, cancel the rest
		// This makes toggling usable and the zoom divisor adjustable
		if (zooming == lastZooming) return;

		boolean doSpyglassSound = OkZoomerConfigManager.CONFIG.tweaks.useSpyglassSounds.value();

		switch (OkZoomerConfigManager.CONFIG.features.zoomMode.value()) {
			case HOLD -> {
				// If the zoom needs to be held, then the zoom signal is determined by if the key is pressed or not
				ZoomUtils.ZOOMER_ZOOM.setZoom(zooming);
				ZoomUtils.resetZoomDivisor(false);
			}
			case TOGGLE -> {
				// If the zoom needs to be toggled, toggle the zoom signal instead
				if (zooming) {
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

		if (doSpyglassSound && !spyglassUse) {
			boolean soundDirection = !OkZoomerConfigManager.CONFIG.features.zoomMode.value().equals(ZoomModes.PERSISTENT)
				? ZoomUtils.ZOOMER_ZOOM.getZoom()
				: keyPress;

			minecraft.player.playSound(soundDirection ? SoundEvents.SPYGLASS_USE : SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
		}

		// Set the previous zoom signal for the next tick
		lastZooming = zooming;
	}
}

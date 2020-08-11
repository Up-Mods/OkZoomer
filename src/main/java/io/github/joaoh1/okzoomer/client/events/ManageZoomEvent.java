package io.github.joaoh1.okzoomer.client.events;

import io.github.joaoh1.okzoomer.client.keybinds.ZoomKeybinds;
import io.github.joaoh1.okzoomer.client.packets.ZoomPackets;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

//This event is responsible for managing the zoom signal.
public class ManageZoomEvent {
    //Used internally in order to make zoom toggling possible.
	private static boolean lastZoomPress = false;

	//Used internally in order to make persistent zoom less buggy.
    private static boolean persistentZoom = false;
    
    public static void registerEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
			//If zoom is disabled, do not allow for zooming at all.
			if (ZoomPackets.disableZoom) {
				return;
			}

			//Handle zoom mode changes.
			if (!OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				if (!persistentZoom) {
					persistentZoom = true;
					lastZoomPress = true;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else {
				if (persistentZoom) {
					persistentZoom = false;
					lastZoomPress = true;
				}
			}

			//If the press state is the same as the previous tick's, cancel the rest. Makes toggling usable and the zoom divisor adjustable.
			if (ZoomKeybinds.zoomKey.isPressed() == lastZoomPress) {
				return;
			}

			if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.HOLD)) {
				//If the zoom needs to be held, then the zoom signal is determined by if the key is pressed or not.
				ZoomUtils.zoomState = ZoomKeybinds.zoomKey.isPressed();
				ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.TOGGLE)) {
				//If the zoom needs to be toggled, toggle the zoom signal instead.
				if (ZoomKeybinds.zoomKey.isPressed()) {
					ZoomUtils.zoomState = !ZoomUtils.zoomState;
					ZoomUtils.zoomDivisor = OkZoomerConfigPojo.values.zoomDivisor;
				}
			} else if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.PERSISTENT)) {
				//If persistent zoom is enabled, just keep the zoom on.
				ZoomUtils.zoomState = true;
			}

			//Manage the post-zoom signal.
			if (!ZoomUtils.zoomState && lastZoomPress) {
				ZoomUtils.lastZoomState = true;
			} else {
				ZoomUtils.lastZoomState = false;
			}

			//Set the previous zoom signal for the next tick.
			lastZoomPress = ZoomKeybinds.zoomKey.isPressed();
		});
    }
}
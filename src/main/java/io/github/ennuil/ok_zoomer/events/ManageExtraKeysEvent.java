package io.github.ennuil.ok_zoomer.events;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;

// This event manages the extra keybinds' behavior
public class ManageExtraKeysEvent implements ClientTickEvents.End {
	@Override
	public void endClientTick(MinecraftClient client) {
		if (!ZoomKeyBinds.areExtraKeyBindsEnabled()) return;
		if (!OkZoomerConfigManager.EXTRA_KEY_BINDS.value()) return;
		if (OkZoomerConfigManager.ZOOM_SCROLLING.isBeingOverridden()) return;

		if (ZoomKeyBinds.DECREASE_ZOOM_KEY.isPressed() && !ZoomKeyBinds.INCREASE_ZOOM_KEY.isPressed()) {
			ZoomUtils.changeZoomDivisor(false);
		}

		if (ZoomKeyBinds.INCREASE_ZOOM_KEY.isPressed() && !ZoomKeyBinds.DECREASE_ZOOM_KEY.isPressed()) {
			ZoomUtils.changeZoomDivisor(true);
		}

		if (ZoomKeyBinds.RESET_ZOOM_KEY.isPressed()) {
			ZoomUtils.resetZoomDivisor(true);
		}
	}
}

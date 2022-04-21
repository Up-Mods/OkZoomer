package io.github.ennuil.okzoomer;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

// This class is responsible for registering the commands and packets
public class OkZoomerClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		// Register all the key binds
		KeyBindingHelper.registerKeyBinding(ZoomKeyBinds.ZOOM_KEY);
		if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
			KeyBindingHelper.registerKeyBinding(ZoomKeyBinds.DECREASE_ZOOM_KEY);
			KeyBindingHelper.registerKeyBinding(ZoomKeyBinds.INCREASE_ZOOM_KEY);
			KeyBindingHelper.registerKeyBinding(ZoomKeyBinds.RESET_ZOOM_KEY);
		}

		// Register the zoom-controlling packets
		ZoomPackets.registerPackets();
	}
}

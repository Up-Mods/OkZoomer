package page.langeweile.ok_zoomer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.events.*;
import page.langeweile.ok_zoomer.key_binds.ZoomKeyBinds;
import page.langeweile.ok_zoomer.utils.FabricZoomUtils;

// This class is responsible for registering the commands and packets
public class OkZoomerClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Initialize the config
		OkZoomerConfigManager.init();

		// Register all the key binds
		KeyMappingHelper.registerKeyMapping(ZoomKeyBinds.ZOOM_KEY);
		if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
			KeyMappingHelper.registerKeyMapping(ZoomKeyBinds.DECREASE_ZOOM_KEY);
			KeyMappingHelper.registerKeyMapping(ZoomKeyBinds.INCREASE_ZOOM_KEY);
			KeyMappingHelper.registerKeyMapping(ZoomKeyBinds.RESET_ZOOM_KEY);
		}

		// Register events without entrypoints aughhhhhhhh
		ClientTickEvents.START_CLIENT_TICK.register(ManageZoomEvent::startClientTick);
		ClientTickEvents.START_CLIENT_TICK.register(client -> ManageExtraKeysEvent.startClientTick());
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> ApplyLoadOnceOptionsEvent.readyClient());
		ClientTickEvents.END_CLIENT_TICK.register(OpenScreenEvent::endClientTick);
		ClientCommandRegistrationCallback.EVENT.register(RegisterCommands::registerCommands);

		FabricZoomUtils.defineSafeSmartOcclusion();
		FabricZoomUtils.addInitialPredicates();
	}
}

package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

@EventBusSubscriber(value = Dist.CLIENT, modid = "ok_zoomer")
public class RegisterEvents {
	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		ZoomKeyBinds.ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
		event.register(ZoomKeyBinds.ZOOM_KEY);
		if (ZoomKeyBinds.areExtraKeyBindsEnabled()) {
			ZoomKeyBinds.DECREASE_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			ZoomKeyBinds.INCREASE_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			ZoomKeyBinds.RESET_ZOOM_KEY.setKeyConflictContext(KeyConflictContext.IN_GAME);
			event.register(ZoomKeyBinds.DECREASE_ZOOM_KEY);
			event.register(ZoomKeyBinds.INCREASE_ZOOM_KEY);
			event.register(ZoomKeyBinds.RESET_ZOOM_KEY);
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		ManageZoomEvent.startClientTick(Minecraft.getInstance());
		ManageExtraKeysEvent.startClientTick(Minecraft.getInstance());
	}

	@SubscribeEvent
	public static void onClientStarted(ClientStartedEvent event) {
		ApplyLoadOnceOptionsEvent.readyClient(event.getClient());
	}

	@SubscribeEvent
	public static void registerCommands(RegisterClientCommandsEvent event) {
		event.getDispatcher().register(
			Commands.literal("ok_zoomer").executes(ctx -> {
				Minecraft.getInstance().setScreen(new OkZoomerConfigScreen(null));
				return 0;
			})
		);
	}
}

package io.github.ennuil.ok_zoomer.events;

import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME, modid = "ok_zoomer")
public class RegisterGameEvents {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		ManageZoomEvent.startClientTick(Minecraft.getInstance());
		ManageExtraKeysEvent.startClientTick(Minecraft.getInstance());
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

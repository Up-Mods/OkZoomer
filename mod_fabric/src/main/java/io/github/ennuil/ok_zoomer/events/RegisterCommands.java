package io.github.ennuil.ok_zoomer.events;

import com.mojang.brigadier.CommandDispatcher;
import io.github.ennuil.ok_zoomer.utils.FabricZoomUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class RegisterCommands {
	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(
			ClientCommandManager.literal("ok_zoomer").executes(ctx -> {
				FabricZoomUtils.setOpenCommandScreen(true);
				return 0;
			}
		));
	}
}

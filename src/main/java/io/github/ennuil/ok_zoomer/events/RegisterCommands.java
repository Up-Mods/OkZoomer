package io.github.ennuil.ok_zoomer.events;

import com.mojang.brigadier.CommandDispatcher;
import io.github.ennuil.ok_zoomer.OkZoomerClientMod;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class RegisterCommands {
	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(
			ClientCommandManager.literal(OkZoomerClientMod.MOD_ID).executes(ctx -> {
				ZoomUtils.setOpenCommandScreen(true);
				return 0;
			}
		));
	}
}

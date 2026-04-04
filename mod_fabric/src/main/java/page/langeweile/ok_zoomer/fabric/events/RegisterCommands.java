package page.langeweile.ok_zoomer.fabric.events;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import page.langeweile.ok_zoomer.fabric.utils.FabricZoomUtils;

public class RegisterCommands {
	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
		dispatcher.register(
			ClientCommands.literal("ok_zoomer").executes(ctx -> {
				FabricZoomUtils.setOpenCommandScreen(true);
				return 0;
			}
		));
	}
}

package io.github.ennuil.okzoomer.commands;

import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public class OkZoomerCommands {
    public static void registerEvent() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("okzoomer").executes(ctx -> {
                ZoomUtils.setOpenCommandScreen(true);
                return 0;
            }
        ));
    }
}

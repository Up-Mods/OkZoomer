package io.github.joaoh1.okzoomer.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import me.shedaniel.fiber2cloth.api.Fiber2Cloth;
import net.minecraft.client.gui.screen.Screen;

public class OkZoomerConfigScreen {
    public static Screen getConfigScreen(Screen parentScreen) {
		return Fiber2Cloth.create(parentScreen, "okzoomer", (ConfigBranch)OkZoomerConfig.tree, "config.okzoomer.title").setSaveRunnable(() -> {
			OkZoomerConfig.saveModConfig();
		}).build().getScreen();
    }
}
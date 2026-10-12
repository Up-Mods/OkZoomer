package page.langeweile.ok_zoomer.fabric.events;

import net.minecraft.client.Minecraft;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;
import page.langeweile.ok_zoomer.fabric.utils.FabricZoomUtils;

public class OpenScreenEvent {
	public static void endClientTick(Minecraft minecraft) {
		if (FabricZoomUtils.shouldOpenCommandScreen()) {
			minecraft.setScreen(new OkZoomerConfigScreen(minecraft.screen));
			FabricZoomUtils.setOpenCommandScreen(false);
		}
	}
}

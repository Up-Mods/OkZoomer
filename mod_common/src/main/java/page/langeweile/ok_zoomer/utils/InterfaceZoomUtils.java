package page.langeweile.ok_zoomer.utils;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.zoom.Zoom;

public class InterfaceZoomUtils {
	private static float translation = 0.0F;
	private static float scale = 1.0F;

	public static void tick(DeltaTracker deltaTracker) {
		float fov = Zoom.getZoomCore().transitionMode().applyZoom(1.0F, deltaTracker.getGameTimeDeltaPartialTick(true));
		InterfaceZoomUtils.translation = 2.0F / ((1.0F / fov) - 1.0F);
		InterfaceZoomUtils.scale = 1.0F / fov;
	}

	public static void zoomInterface(GuiGraphicsExtractor graphics, Runnable runnable) {
		graphics.pose().pushMatrix();
		graphics.pose().translate(-(graphics.guiWidth() / InterfaceZoomUtils.translation), -(graphics.guiHeight() / InterfaceZoomUtils.translation));
		graphics.pose().scale(InterfaceZoomUtils.scale, InterfaceZoomUtils.scale);
		runnable.run();
		graphics.pose().popMatrix();
	}

	// TODO - This assumes that the popped matrix is the interface zoom one, find a better way to do this!
	public static void breakoutInterface(GuiGraphicsExtractor graphics, Runnable runnable) {
		if (OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value() || !Zoom.getZoomCore().transitionMode().getActive()) {
			runnable.run();
		} else {
			graphics.pose().popMatrix();
			runnable.run();
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / InterfaceZoomUtils.translation), -(graphics.guiHeight() / InterfaceZoomUtils.translation));
			graphics.pose().scale(InterfaceZoomUtils.scale, InterfaceZoomUtils.scale);
		}
	}
}

package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(SubtitleOverlay.class)
public abstract class SubtitleOverlayMixin {
	@WrapMethod(method = "extractRenderState")
	private void zoomGui(GuiGraphicsExtractor graphics, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.tweaks.persistentCaptions.value()
			|| OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value()
			|| !Zoom.getTransitionMode().getActive()
		) {
			original.call(graphics);
		} else {
			float fov = Zoom.getTransitionMode().applyZoom(1.0F, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
			float translation = 2.0F / ((1.0F / fov) - 1.0F);
			float scale = 1.0F / fov;
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation));
			graphics.pose().scale(scale, scale);
			original.call(graphics);
			graphics.pose().popMatrix();
		}
	}
}

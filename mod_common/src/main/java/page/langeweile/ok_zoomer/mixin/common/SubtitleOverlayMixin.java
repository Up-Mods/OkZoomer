package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.InterfaceZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(SubtitleOverlay.class)
public abstract class SubtitleOverlayMixin {
	@WrapMethod(method = "extractRenderState")
	private void zoomGui(GuiGraphicsExtractor graphics, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.tweaks.persistentCaptions.value()
			|| OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value()
			|| !Zoom.getZoomCore().transitionMode().getActive()
		) {
			original.call(graphics);
		} else {
			InterfaceZoomUtils.zoomInterface(graphics, () -> original.call(graphics));
		}
	}
}

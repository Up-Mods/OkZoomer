package page.langeweile.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ForgeZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {
	@WrapMethod(method = "render")
	private void zoomGui(GuiGraphics graphics, float partialTick, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.features.persistentInterface.value() || !page.langeweile.ok_zoomer.zoom.Zoom.getTransitionMode().getActive()) {
			original.call(graphics, partialTick);
		} else {
			float fov = Zoom.getTransitionMode().applyZoom(1.0F, partialTick);
			ForgeZoomUtils.translation = 2.0F / ((1.0F / fov) - 1.0F);
			ForgeZoomUtils.scale = 1.0F / fov;
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
			original.call(graphics, partialTick);
			graphics.pose().popPose();
		}
	}

	@ModifyExpressionValue(
		method = "renderSpyglassOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z")
	)
	private boolean activateSpyglassOverlay(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}

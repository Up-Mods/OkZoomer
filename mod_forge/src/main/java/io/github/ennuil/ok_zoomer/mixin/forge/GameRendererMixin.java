package io.github.ennuil.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ForgeZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V"))
	private void zoomGui(Gui instance, GuiGraphics graphics, float partialTick, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.features.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(instance, graphics, partialTick);
		} else {
			float fov = Zoom.getTransitionMode().applyZoom(1.0F, partialTick);
			ForgeZoomUtils.translation = 2.0F / ((1.0F / fov) - 1.0F);
			ForgeZoomUtils.scale = 1.0F / fov;
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
			original.call(instance, graphics, partialTick);
			graphics.pose().popPose();
		}
	}
}

package io.github.ennuil.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ForgeZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@WrapMethod(method = "renderCrosshair")
	private void hideCrosshair(GuiGraphics graphics, Operation<Void> original) {
		boolean persistentInterface = OkZoomerConfigManager.CONFIG.features.persistentInterface.value();
		boolean hideCrosshair = OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value();
		if (persistentInterface || hideCrosshair || !Zoom.isTransitionActive()) {
			if (hideCrosshair) {
				float fade = 1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getFrameTime());
				RenderSystem.setShaderColor(fade, fade, fade, fade);
			}
			original.call(graphics);
			if (hideCrosshair) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		} else {
			// TODO - This has been recycled once, this should become a method
			var lastPose = graphics.pose().last().pose();
			graphics.pose().popPose();
			graphics.pose().popPose();
			graphics.pose().pushPose();
			graphics.pose().translate(0.0F, 0.0F, lastPose.getTranslation(new Vector3f()).z);
			original.call(graphics);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
			graphics.pose().pushPose();
		}
	}
}

package io.github.ennuil.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ForgeZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VanillaGuiOverlay.class)
public abstract class VanillaGuiOverlayMixin {
	// TODO - This is a very promising method to get individual HUDs persistent, but I'm not sure if it's bulletproof!
	// It doesn't crash with Sodium nor ImmediatelyFast though, and that's good
	@WrapOperation(
		method = "lambda$static$18",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderHUDText(IILnet/minecraft/client/gui/GuiGraphics;)V"
		)
	)
	private static void ensureDebugHudVisibility(ForgeGui instance, int screenWidth, int screenHeight, GuiGraphics graphics, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.features.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(instance, screenWidth, screenHeight, graphics);
		} else {
			var lastPose = graphics.pose().last().pose();
			graphics.pose().popPose();
			graphics.pose().pushPose();
			graphics.pose().translate(0.0F, 0.0F, lastPose.getTranslation(new Vector3f()).z);
			original.call(instance, screenWidth, screenHeight, graphics);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
			graphics.pose().pushPose();
		}
	}
}

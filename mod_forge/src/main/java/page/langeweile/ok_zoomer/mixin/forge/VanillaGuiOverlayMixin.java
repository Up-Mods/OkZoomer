package page.langeweile.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ForgeZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(VanillaGuiOverlay.class)
public abstract class VanillaGuiOverlayMixin {
	@WrapOperation(
		method = "lambda$static$18",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderHUDText(IILnet/minecraft/client/gui/GuiGraphics;)V"
		),
		remap = false
	)
	private static void ensureDebugHudVisibility(ForgeGui instance, int screenWidth, int screenHeight, GuiGraphics graphics, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(instance, screenWidth, screenHeight, graphics);
		} else {
			graphics.pose().popPose();
			original.call(instance, screenWidth, screenHeight, graphics);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
		}
	}

	@WrapOperation(
		method = "lambda$static$22",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderSubtitles(Lnet/minecraft/client/gui/GuiGraphics;)V"
		),
		remap = false
	)
	private static void ensureSubtitlesVisibility(ForgeGui instance, GuiGraphics graphics, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(instance, graphics);
		} else {
			graphics.pose().popPose();
			original.call(instance, graphics);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / ForgeZoomUtils.translation), -(graphics.guiHeight() / ForgeZoomUtils.translation), 0.0F);
			graphics.pose().scale(ForgeZoomUtils.scale, ForgeZoomUtils.scale, 1.0F);
		}
	}
}

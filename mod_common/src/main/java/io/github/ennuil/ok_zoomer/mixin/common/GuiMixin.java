package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
	@Unique
	private float translation = 0.0F;

	@Unique
	private float scale = 0.0F;

	@Inject(
		method = "renderCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaTicks()F"
		)
	)
	private void injectZoomOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Share("cancelOverlay") LocalBooleanRef cancelOverlay) {
		cancelOverlay.set(false);
		if (Zoom.getZoomOverlay() != null) {
			var overlay = Zoom.getZoomOverlay();
			overlay.tickBeforeRender(deltaTracker);
			if (overlay.getActive()) {
				cancelOverlay.set(overlay.cancelOverlayRendering());
				overlay.renderOverlay(graphics, deltaTracker, Zoom.getTransitionMode());
			}
		}
	}

	// Cancel the cancellable overlays
	@ModifyExpressionValue(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
	private boolean cancelOverlay(boolean original, @Share("cancelOverlay") LocalBooleanRef cancelOverlay) {
		return original && !cancelOverlay.get();
	}

	@ModifyExpressionValue(
		method = "renderCameraOverlays",
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

	@WrapMethod(method = "render")
	private void zoomGui(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.features.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(graphics, deltaTracker);
		} else {
			float fov = Zoom.getTransitionMode().applyZoom(1.0F, deltaTracker.getGameTimeDeltaPartialTick(true));
			translation = 2.0F / ((1.0F / fov) - 1.0F);
			scale = 1.0F / fov;
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation));
			graphics.pose().scale(scale, scale);
			original.call(graphics, deltaTracker);
			graphics.pose().popMatrix();
		}
	}

	@WrapMethod(method = "renderCrosshair")
	private void hideCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		boolean persistentInterface = OkZoomerConfigManager.CONFIG.features.persistentInterface.value();
		boolean hideCrosshair = OkZoomerConfigManager.CONFIG.features.hideCrosshair.value();
		if (persistentInterface || hideCrosshair || !Zoom.isTransitionActive()) {
			original.call(graphics, deltaTracker);
		} else {
			// TODO - This fix is too suspicious, test it on pre-releases
			graphics.pose().popMatrix();
			original.call(graphics, deltaTracker);
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation));
			graphics.pose().scale(scale, scale);
		}
	}

	// The "fade the whole pipeline" approach was too good to last forever,
	// We'll just fade on GuiGraphics level
	@WrapMethod(method = "renderCrosshair")
	private void fadeCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.features.hideCrosshair.value()) {
			ZoomUtils.setFadeModifier(1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)));
			original.call(guiGraphics, deltaTracker);
			ZoomUtils.setFadeModifier(null);
		} else {
			original.call(guiGraphics, deltaTracker);
		}
	}
}

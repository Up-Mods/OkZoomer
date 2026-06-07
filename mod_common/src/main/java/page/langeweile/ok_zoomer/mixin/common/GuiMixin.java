package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

// This mixin should have a higher priority than Fabric API for compatibility reasons
@Mixin(value = Hud.class, priority = 998)
public abstract class GuiMixin {
	@Unique
	private float translation = 0.0F;

	@Unique
	private float scale = 0.0F;

	@Inject(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaTicks()F"
		)
	)
	private void injectZoomOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Share("cancelOverlay") LocalBooleanRef cancelOverlay) {
		cancelOverlay.set(false);
		if (Zoom.getZoomOverlay() != null) {
			var overlay = Zoom.getZoomOverlay();
			overlay.tickBeforeRender(deltaTracker);
			if (overlay.getActive()) {
				cancelOverlay.set(overlay.cancelOverlayRendering());
				overlay.extractOverlay(graphics, deltaTracker, Zoom.getTransitionMode());
			}
		}
	}

	// Cancel the cancellable overlays
	@ModifyExpressionValue(method = "extractCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
	private boolean cancelOverlay(boolean original, @Share("cancelOverlay") LocalBooleanRef cancelOverlay) {
		return original && !cancelOverlay.get();
	}

	@ModifyExpressionValue(
		method = "extractCameraOverlays",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z")
	)
	private boolean activateSpyglassOverlay(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.controls.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}

	@WrapMethod(method = "extractRenderState")
	private void zoomGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(graphics, deltaTracker);
		} else {
			float fov = Zoom.getTransitionMode().applyZoom(1.0F, deltaTracker.getGameTimeDeltaPartialTick(true));
			this.translation = 2.0F / ((1.0F / fov) - 1.0F);
			this.scale = 1.0F / fov;
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / this.translation), -(graphics.guiHeight() / this.translation));
			graphics.pose().scale(this.scale, this.scale);
			original.call(graphics, deltaTracker);
			graphics.pose().popMatrix();
		}
	}

	@WrapMethod(method = "extractCrosshair")
	private void hideCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		boolean persistentInterface = OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value();
		boolean hideCrosshair = OkZoomerConfigManager.CONFIG.appearance.hideCrosshair.value();
		if (persistentInterface || hideCrosshair || !Zoom.isTransitionActive()) {
			original.call(graphics, deltaTracker);
		} else {
			graphics.pose().popMatrix();
			original.call(graphics, deltaTracker);
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / this.translation), -(graphics.guiHeight() / this.translation));
			graphics.pose().scale(this.scale, this.scale);
		}
	}

	// The "fade the whole pipeline" approach was too good to last forever,
	// We'll just fade on GuiGraphics level
	@WrapMethod(method = "extractCrosshair")
	private void fadeCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.hideCrosshair.value()) {
			ZoomUtils.setFadeModifier(1.0F - Zoom.getTransitionMode().getFade(deltaTracker.getGameTimeDeltaPartialTick(true)));
			original.call(guiGraphics, deltaTracker);
			ZoomUtils.setFadeModifier(null);
		} else {
			original.call(guiGraphics, deltaTracker);
		}
	}
}

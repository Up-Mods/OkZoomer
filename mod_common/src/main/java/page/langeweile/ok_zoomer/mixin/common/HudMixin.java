package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.InterfaceZoomUtils;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

// This mixin should have a higher priority than Fabric API for compatibility reasons
@Mixin(value = Hud.class, priority = 998)
public abstract class HudMixin {
	@Inject(
		method = "extractCameraOverlays",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaTicks()F"
		)
	)
	private void injectZoomOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci, @Share("cancelOverlay") LocalBooleanRef cancelOverlay) {
		cancelOverlay.set(false);
		if (Zoom.getZoomCore().overlay() != null) {
			var overlay = Zoom.getZoomCore().overlay();
			overlay.tickBeforeRender(deltaTracker);
			if (overlay.getActive()) {
				cancelOverlay.set(overlay.cancelOverlayRendering());
				InterfaceZoomUtils.breakoutInterface(graphics, () -> overlay.extractOverlay(graphics, deltaTracker, Zoom.getZoomCore().transitionMode()));
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
		if (OkZoomerConfigManager.CONFIG.appearance.persistentInterface.value() || !Zoom.getZoomCore().transitionMode().getActive()) {
			original.call(graphics, deltaTracker);
		} else {
			InterfaceZoomUtils.tick(deltaTracker);
			InterfaceZoomUtils.zoomInterface(graphics, () -> original.call(graphics, deltaTracker));
		}
	}

	@WrapMethod(method = "extractCrosshair")
	private void hideCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.hideCrosshair.value()) {
			original.call(graphics, deltaTracker);
		} else {
			InterfaceZoomUtils.breakoutInterface(graphics, () -> original.call(graphics, deltaTracker));
		}
	}

	// The "fade the whole pipeline" approach was too good to last forever,
	// We'll just fade on GuiGraphics level
	@WrapMethod(method = "extractCrosshair")
	private void fadeCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.appearance.hideCrosshair.value()) {
			ZoomUtils.setFadeModifier(1.0F - Zoom.getZoomCore().transitionMode().getFade(deltaTracker.getGameTimeDeltaPartialTick(true)));
			original.call(guiGraphics, deltaTracker);
			ZoomUtils.setFadeModifier(null);
		} else {
			original.call(guiGraphics, deltaTracker);
		}
	}
}

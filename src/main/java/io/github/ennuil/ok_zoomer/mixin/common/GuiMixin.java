package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Unique
	private boolean hideCrossbar = false;

	@Shadow
	protected abstract void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

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
			hideCrossbar = false;
			if (!OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value()) {
				graphics.pose().pushPose();
				graphics.pose().translate(0.0F, 0.0F, 200.0F);
				this.renderCrosshair(graphics, deltaTracker);
				graphics.pose().popPose();
				hideCrossbar = true;
			}
			double fov = Zoom.getTransitionMode().applyZoom(1.0F, deltaTracker.getGameTimeDeltaPartialTick(true));
			float divisor = (float) (1.0D / fov);
			double translation = 2.0D / ((1.0D / fov) - 1);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / (translation)), -(graphics.guiHeight() / (translation)), 0.0F);
			graphics.pose().scale(divisor, divisor, divisor);
			original.call(graphics, deltaTracker);
			graphics.pose().popPose();
		}
	}

	@WrapMethod(method = "renderCrosshair")
	private void hideCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		if (!hideCrossbar) {
			if (!OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value() || !Zoom.getTransitionMode().getActive()) {
				original.call(graphics, deltaTracker);
			} else {
				float fade = 1.0F - (float) Zoom.getTransitionMode().getFade(deltaTracker.getGameTimeDeltaPartialTick(true));
				RenderSystem.setShaderColor(fade, fade, fade, fade);
				original.call(graphics, deltaTracker);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		} else {
			hideCrossbar = false;
		}
	}
}

package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.joml.Vector3f;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
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
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation), 0.0F);
			graphics.pose().scale(scale, scale, 1.0F);
			original.call(graphics, deltaTracker);
			graphics.pose().popPose();
		}
	}

	@WrapMethod(method = "renderCrosshair")
	private void hideCrosshair(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
		boolean persistentInterface = OkZoomerConfigManager.CONFIG.features.persistentInterface.value();
		boolean hideCrosshair = OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value();
		if ((persistentInterface && !hideCrosshair) || !Zoom.isTransitionActive()) {
			original.call(graphics, deltaTracker);
		} else {
			if (hideCrosshair) {
				float fade = 1.0F - Zoom.getTransitionMode().getFade(deltaTracker.getGameTimeDeltaPartialTick(true));
				RenderSystem.setShaderColor(fade, fade, fade, fade);
			}
			if (!persistentInterface && !hideCrosshair) {
				// TODO - This has been recycled once, this should become a method
				var lastPose = graphics.pose().last().pose();
				graphics.pose().popPose();
				graphics.pose().popPose();
				graphics.pose().pushPose();
				graphics.pose().translate(0.0F, 0.0F, lastPose.getTranslation(new Vector3f()).z);
				original.call(graphics, deltaTracker);
				graphics.pose().pushPose();
				graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation), 0.0F);
				graphics.pose().scale(scale, scale, 1.0F);
			} else {
				original.call(graphics, deltaTracker);
			}
			if (hideCrosshair) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	// TODO - This is a very promising method to get individual HUDs persistent, but I'm not sure if it's bulletproof!
	// It doesn't crash with Sodium nor ImmediatelyFast though, and that's good
	@Dynamic
	@WrapOperation(
		method = {
			"method_55807",
			"lambda$new$6" // Heck yeah! It's Neo-specific hack time!
		},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"
		),
		allow = 1
	)
	private void ensureDebugHudVisibility(DebugScreenOverlay instance, GuiGraphics graphics, Operation<Void> original, @Local(argsOnly = true) DeltaTracker deltaTracker) {
		if (OkZoomerConfigManager.CONFIG.features.persistentInterface.value() || !Zoom.getTransitionMode().getActive()) {
			original.call(instance, graphics);
		} else {
			var lastPose = graphics.pose().last().pose();
			graphics.pose().popPose();
			graphics.pose().popPose();
			graphics.pose().pushPose();
			graphics.pose().translate(0.0F, 0.0F, lastPose.getTranslation(new Vector3f()).z);
			original.call(instance, graphics);
			graphics.pose().pushPose();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation), 0.0F);
			graphics.pose().scale(scale, scale, 1.0F);
		}
	}
}

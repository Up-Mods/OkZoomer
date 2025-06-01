package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
		boolean hideCrosshair = OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value();
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

	// TODO - This is a very promising method to get individual HUDs persistent, but I'm not sure if it's bulletproof!
	// It doesn't crash with Sodium nor ImmediatelyFast though, and that's good
	@WrapOperation(
		method = "renderDebugOverlay",
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
			graphics.pose().popMatrix();
			original.call(instance, graphics);
			graphics.pose().pushMatrix();
			graphics.pose().translate(-(graphics.guiWidth() / translation), -(graphics.guiHeight() / translation));
			graphics.pose().scale(scale, scale);

		}
	}

	// The "fade the whole pipeline" approach was too good to last forever,
	// We'll just fade the crosshair sprites one-by-one
	@WrapOperation(
		method = "renderCrosshair",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"
		)
	)
	private void fadeCrosshair(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value()) {
			float fade = 1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
			instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, ARGB.colorFromFloat(fade, fade, fade, fade));
		} else {
			original.call(instance, renderPipeline, resourceLocation, i, j, k, l);
		}
	}

	@WrapOperation(
		method = "renderCrosshair",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"
		)
	)
	private void fadeCrosshairAttack(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, Operation<Void> original) {
		if (OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value()) {
			float fade = 1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
			instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, m, n, o, p, ARGB.colorFromFloat(fade, fade, fade, fade));
		} else {
			original.call(instance, renderPipeline, resourceLocation, i, j, k, l, m, n, o, p);
		}
	}
}

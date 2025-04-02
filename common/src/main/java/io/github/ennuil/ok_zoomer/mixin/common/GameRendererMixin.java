package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(method = "tick()V", at = @At("HEAD"))
	private void tickInstances(CallbackInfo info) {
		boolean zooming = Zoom.isZooming();
		if (zooming || (Zoom.isTransitionActive() || Zoom.isModifierActive() || Zoom.isOverlayActive())) {
			double divisor = zooming ? Zoom.getZoomDivisor() : 1.0;
			Zoom.getTransitionMode().tick(zooming, divisor);
			if (Zoom.getMouseModifier() != null) {
				Zoom.getMouseModifier().tick(zooming);
			}
			if (Zoom.getZoomOverlay() != null) {
				Zoom.getZoomOverlay().tick(zooming, divisor, Zoom.getTransitionMode());
			}
		}
	}

	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Integer;intValue()I",
			remap = false
		)
	)
	private int modifyCulling(int original) {
		if (!Zoom.isZooming() || !ZoomUtils.hasSmartOcclusion()) {
			return original;
		} else {
			return Mth.positiveCeilDiv(original, Math.max(1, Mth.floor(Zoom.getZoomDivisor())));
		}
	}

	@ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
	private double modifyFov(double original, @Local(argsOnly = true) float partialTicks) {
		if (!Zoom.isTransitionActive()) {
			return original;
		} else {
			return Zoom.getTransitionMode().applyZoom((float) original, partialTicks);
		}
	}

	@ModifyExpressionValue(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
	private float modifyBob(float bob, @Local(argsOnly = true) float delta) {
		if (!Zoom.isZooming() || !OkZoomerConfigManager.CONFIG.features.reduceViewBobbing.value()) {
			return bob;
		} else {
			return Zoom.getTransitionMode().applyZoom(bob, delta);
		}
	}
}

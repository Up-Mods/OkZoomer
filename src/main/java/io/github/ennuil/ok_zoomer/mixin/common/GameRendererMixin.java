package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
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
		if (!Zoom.isZooming()) {
			return original;
		} else {
			return Mth.positiveCeilDiv(original, Mth.floor(Zoom.getZoomDivisor()));
		}
	}

	@Inject(
		method = "getFov",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;",
			ordinal = 0
		)
	)
	private void getZoomedFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir, @Local(ordinal = 1) LocalFloatRef internalFov) {
		float zoomedFov = internalFov.get();

		if (Zoom.isTransitionActive()) {
			zoomedFov = Zoom.getTransitionMode().applyZoom(zoomedFov, partialTicks);
		}

		internalFov.set(zoomedFov);
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

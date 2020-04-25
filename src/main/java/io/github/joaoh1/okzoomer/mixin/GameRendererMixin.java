package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.joaoh1.okzoomer.OkZoomerMod;
import io.github.joaoh1.okzoomer.config.OkZoomerConfig;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(at = @At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
	private double getZoomedFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> info) {
		double fov = info.getReturnValueD();
		if (OkZoomerMod.isZoomKeyPressed) {
			fov /= OkZoomerConfig.zoomDivisor.getValue();
			info.setReturnValue(fov);
		}
		return fov;
	}
}

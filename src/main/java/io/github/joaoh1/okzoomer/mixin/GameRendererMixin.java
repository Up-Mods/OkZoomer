package io.github.joaoh1.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.joaoh1.okzoomer.OkZoomerMod;
import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;

// TODO - Comment the code
@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private final MinecraftClient client = MinecraftClient.getInstance();

	private float zoomFovMultiplier;
	private float lastZoomFovMultiplier;

	private void updateZoomFovMultiplier() {
		float zoomMultiplier = 1.0F;
		float multiplierMultiplier = 0.75F;

		if (OkZoomerMod.isZoomKeyPressed) {
			zoomMultiplier /= OkZoomerMod.zoomDivisor;
			if (OkZoomerMod.zoomDivisor >= 1.0) {
				multiplierMultiplier = 1.0F - zoomMultiplier;
			}
		}

		this.lastZoomFovMultiplier = this.zoomFovMultiplier;
		this.zoomFovMultiplier += (zoomMultiplier - this.zoomFovMultiplier) * multiplierMultiplier;
	 }

	@Inject(at = @At("HEAD"), method = "tick()V")
	private void zoomFovMultiplierTick(CallbackInfo info) {
		if (OkZoomerConfig.smoothTransition.getValue()) {
			this.updateZoomFovMultiplier();
		}
	}
	
	@Inject(at = @At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
	private double getZoomedFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> info) {
		double fov = info.getReturnValueD();

		if (OkZoomerConfig.smoothTransition.getValue()) {
			if (this.zoomFovMultiplier != 1.0F) {
				fov *= (double)MathHelper.lerp(tickDelta, this.lastZoomFovMultiplier, this.zoomFovMultiplier);
				info.setReturnValue(fov);
			}
		} else {
			if (OkZoomerMod.isZoomKeyPressed) {
				double zoomedFov = fov / OkZoomerMod.zoomDivisor;
				info.setReturnValue(zoomedFov);
			}
		}

		if (OkZoomerMod.zoomHasHappened) {
			if (changingFov) {
				this.client.worldRenderer.scheduleTerrainUpdate();
			}
		}

		return fov;
	}
}

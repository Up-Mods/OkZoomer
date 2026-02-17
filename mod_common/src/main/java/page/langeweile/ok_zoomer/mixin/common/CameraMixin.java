package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private int matrixPropertiesDirty;

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

	@ModifyReturnValue(method = "calculateFov", at = @At("TAIL"))
	private float modifyFov(float original, @Local(argsOnly = true) float partialTicks) {
		if (!Zoom.isTransitionActive()) {
			return original;
		} else {
			return Zoom.getTransitionMode().applyZoom(original, partialTicks);
		}
	}

	@ModifyReturnValue(method = "calculateHudFov", at = @At("RETURN"))
	private float modifyHandFov(float original, @Local(argsOnly = true) float partialTicks) {
		if (!Zoom.isTransitionActive() && !OkZoomerConfigManager.CONFIG.appearance.zoomHands.value()) {
			return original;
		} else {
			return Zoom.getTransitionMode().applyZoom(original, partialTicks);
		}
	}

	// TODO - Fix culling more thoroughly
	/*
	@ModifyArg(
		method = "createProjectionMatrixForCulling",
		at = @At(
			value = "INVOKE",
			target = "Lorg/joml/Matrix4f;perspective(FFFFZ)Lorg/joml/Matrix4f;"
		),
		index = 0
	)
	private float modifyCulling(float original) {
		if (!Zoom.isZooming()) {
			return original;
		} else {
			this.matrixPropertiesDirty |= 3;
			return (float) (original * Zoom.getTransitionMode().getInternalMultiplier());
		}
	}
	 */
}

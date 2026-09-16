package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "tick()V", at = @At("HEAD"))
	private void tickInstances(CallbackInfo info) {
		var zoomCore = Zoom.getZoomCore();
		boolean zooming = Zoom.isZooming();
		boolean transitionActive = zoomCore.transitionMode().getActive();

		if (zooming || transitionActive) {
			zoomCore.transitionMode().tick(zooming, zooming ? Zoom.getZoomDivisor() : 1.0F);

			if (zoomCore.mouseModifier() != null) {
				zoomCore.mouseModifier().tick(zooming, transitionActive);
			}

			if (zoomCore.overlay() != null) {
				zoomCore.overlay().tick(zooming, transitionActive);
			}
		}
	}

	@ModifyReturnValue(method = "calculateFov", at = @At("TAIL"))
	private float modifyFov(float original, @Local(argsOnly = true) float partialTicks) {
		if (!Zoom.getZoomCore().transitionMode().getActive()) {
			return original;
		} else {
			return Zoom.getZoomCore().transitionMode().applyZoom(original, partialTicks);
		}
	}

	// TODO - This affects the debug crosshair as well, make it not affect that!
	@ModifyReturnValue(method = "calculateHudFov", at = @At("RETURN"))
	private float modifyHandFov(float original, @Local(argsOnly = true) float partialTicks) {
		if (!Zoom.getZoomCore().transitionMode().getActive() || !OkZoomerConfigManager.CONFIG.appearance.zoomHands.value()) {
			return original;
		} else {
			return Zoom.getZoomCore().transitionMode().applyZoom(original, partialTicks);
		}
	}

	@ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/ClientAvatarState;getInterpolatedBob(F)F"))
	private float modifyBob(float bob, @Local(argsOnly = true) CameraRenderState cameraState) {
		if (!Zoom.isZooming() || !OkZoomerConfigManager.CONFIG.appearance.reduceViewBobbing.value()) {
			return bob;
		} else {
			return Zoom.getZoomCore().transitionMode().applyZoom(bob, cameraState.cameraEntityPartialTicks);
		}
	}

	@ModifyArg(
		method = "createProjectionMatrixForCulling",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;max(FF)F"
		),
		index = 1
	)
	private float modifyCullingFov(float original) {
		if (!ZoomUtils.hasSmartOcclusion() || !Zoom.isZooming()) {
			return original;
		} else {
			// Can't be arbitrarily small or else glitching can occur
			return 5.0F;
		}
	}
}

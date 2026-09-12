package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {
	@Unique
	private float prevCamFov = Float.MIN_VALUE;

	@ModifyExpressionValue(
		method = "extract",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;consumeFrustumUpdate()Z"
		)
	)
	private boolean modifyFrustumUpdateCheck(boolean original, @Local(argsOnly = true) Camera camera) {
		return original || camera.getFov() != this.prevCamFov;
	}

	@Inject(
		method = "extract",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/extract/LevelExtractor;applyFrustum(Lnet/minecraft/client/renderer/culling/Frustum;)V"
		)
	)
	private void setPrevCamFov(DeltaTracker deltaTracker, Camera camera, float worldPartialTicks, CallbackInfo ci) {
		this.prevCamFov = camera.getFov();
	}
}

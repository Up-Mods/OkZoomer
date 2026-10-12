package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Sodium uses @Overwrite on this method (and equivalents) until 26.2, so we need to be crafty
@Mixin(value = LevelRenderer.class, priority = 1100)
public abstract class LevelRendererMixin {
	@Unique
	private float prevCamFov = Float.MIN_VALUE;

	@ModifyExpressionValue(
		method = "cullTerrain",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/SectionOcclusionGraph;consumeFrustumUpdate()Z"
		),
		require = 0
	)
	private boolean modifyFrustumUpdateCheck(boolean original, @Local(argsOnly = true) Camera camera) {
		return original || camera.getFov() != this.prevCamFov;
	}

	@Inject(
		method = "cullTerrain",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/LevelRenderer;applyFrustum(Lnet/minecraft/client/renderer/culling/Frustum;)V"
		),
		require = 0
	)
	private void setPrevCamFov(Camera camera, Frustum frustum, boolean spectator, CallbackInfo ci) {
		this.prevCamFov = camera.getFov();
	}
}

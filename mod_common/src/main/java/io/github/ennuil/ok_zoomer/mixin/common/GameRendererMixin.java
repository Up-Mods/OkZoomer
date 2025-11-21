package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.CachedPerspectiveProjectionMatrixBuffer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private CachedPerspectiveProjectionMatrixBuffer hud3dProjectionMatrixBuffer;

	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private static boolean is3DCrosshair;

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
		method = "getProjectionMatrixForCulling",
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
	private float modifyFov(float original, @Local(argsOnly = true) float partialTicks, @Local(argsOnly = true) boolean useFovSetting) {
		if (!Zoom.isTransitionActive() || is3DCrosshair || (!useFovSetting && !OkZoomerConfigManager.CONFIG.features.zoomHands.value())) {
			return original;
		} else {
			return Zoom.getTransitionMode().applyZoom(original, partialTicks);
		}
	}

	@ModifyExpressionValue(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/ClientAvatarState;getInterpolatedBob(F)F"))
	private float modifyBob(float bob, @Local(argsOnly = true) float delta) {
		if (!Zoom.isZooming() || !OkZoomerConfigManager.CONFIG.features.reduceViewBobbing.value()) {
			return bob;
		} else {
			return Zoom.getTransitionMode().applyZoom(bob, delta);
		}
	}

	@WrapOperation(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render3dCrosshair(Lnet/minecraft/client/Camera;)V"
		)
	)
	private void prevent3DCrosshairZoom(DebugScreenOverlay instance, Camera camera, Operation<Void> original, @Local(ordinal = 0) float f) {
		if (!Zoom.isTransitionActive()) {
			original.call(instance, camera);
		} else {
			is3DCrosshair = true;
			var buffer = RenderSystem.getProjectionMatrixBuffer();
			var type = RenderSystem.getProjectionType();
			RenderSystem.setProjectionMatrix(
				this.hud3dProjectionMatrixBuffer.getBuffer(
					this.minecraft.getWindow().getWidth(),
					this.minecraft.getWindow().getHeight(),
					this.getFov(camera, f, false)
				),
				ProjectionType.PERSPECTIVE
			);
			original.call(instance, camera);
			is3DCrosshair = false;
			RenderSystem.setProjectionMatrix(buffer, type);
		}
	}

	@Shadow
	protected abstract float getFov(Camera camera, float partialTick, boolean useFovSetting);
}

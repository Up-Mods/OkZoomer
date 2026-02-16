package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	/*
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
	*/
}

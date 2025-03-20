package io.github.ennuil.ok_zoomer.mixin.common.fade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.opengl.GlProgram;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlProgram.class)
public abstract class GlProgramMixin {
	@ModifyExpressionValue(
		method = "setDefaultUniforms",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/systems/RenderSystem;getShaderColor()[F"
		)
	)
	private float[] modifyColorUniform(float[] original) {
		// Conditions done here and on this specific order since doing a config check for every pipeline *has* overhead
		if (ZoomUtils.shouldModifyPipeline() && OkZoomerConfigManager.CONFIG.tweaks.hideCrosshair.value()) {
			float fade = 1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
			return new float[] { original[0] * fade, original[1] * fade, original[2] * fade, original[3] * fade };
		} else {
			return original;
		}
	}
}

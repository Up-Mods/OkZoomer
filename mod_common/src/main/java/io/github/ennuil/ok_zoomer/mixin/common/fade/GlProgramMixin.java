package io.github.ennuil.ok_zoomer.mixin.common.fade;

import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GlProgram.class)
public abstract class GlProgramMixin {
	@Shadow
	public abstract @Nullable Uniform getUniform(String string);

	@Unique
	public Uniform CROSSHAIR_FADE;


	@Inject(method = "setupUniforms", at = @At(value = "TAIL"))
	private void setupCrosshairFadeUniform(List<RenderPipeline.UniformDescription> list, List<String> list2, CallbackInfo ci) {
		this.CROSSHAIR_FADE = this.getUniform("OkZoomerCrosshairFade");
	}

	@Inject(method = "setDefaultUniforms", at = @At(value = "TAIL"))
	private void setupCrosshairFadeUniform2(VertexFormat.Mode mode, Matrix4f matrix4f, Matrix4f matrix4f2, float f, float g, CallbackInfo ci) {
		if (this.CROSSHAIR_FADE != null) {
			this.CROSSHAIR_FADE.set(1.0F - Zoom.getTransitionMode().getFade(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)));
		}
	}
}

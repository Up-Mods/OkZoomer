package io.github.ennuil.ok_zoomer.mixin.common.fade;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GlCommandEncoder.class)
public abstract class GlCommandEncoderMixin {
	@WrapOperation(
		method = "trySetup",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/opengl/GlProgram;setDefaultUniforms(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FF)V"
		)
	)
	private void wrapSetup(GlProgram instance, VertexFormat.Mode mode, Matrix4f matrix4f, Matrix4f matrix4f2, float f, float g, Operation<Void> original, @Local RenderPipeline pipeline) {
		// TODO - Keep an eye on the identifier check
		if (pipeline.getLocation().getPath().equals("pipeline/crosshair")) {
			ZoomUtils.setModifyPipeline(true);
			original.call(instance, mode, matrix4f, matrix4f2, f, g);
			ZoomUtils.setModifyPipeline(false);
		} else {
			original.call(instance, mode, matrix4f, matrix4f2, f, g);
		}
	}
}

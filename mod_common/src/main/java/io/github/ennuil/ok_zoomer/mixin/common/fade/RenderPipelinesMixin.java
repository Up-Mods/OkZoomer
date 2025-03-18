package io.github.ennuil.ok_zoomer.mixin.common.fade;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import io.github.ennuil.ok_zoomer.utils.ModUtils;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderPipelines.class)
public abstract class RenderPipelinesMixin {
	// TODO - Figure out a way to do this without shaders once more
	// TODO - "Hide Crosshair" can be disabled, make it so it also reverts to the Vanilla shader if disabled
	@ModifyReceiver(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;build()Lcom/mojang/blaze3d/pipeline/RenderPipeline;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				args = "stringValue=pipeline/crosshair"
			)
		)
	)
	private static RenderPipeline.Builder addFadeShader(RenderPipeline.Builder instance) {
		return instance
			.withFragmentShader(ModUtils.id("core/rendertype_crosshair"))
			.withUniform("OkZoomerCrosshairFade", UniformType.FLOAT);
	}
}

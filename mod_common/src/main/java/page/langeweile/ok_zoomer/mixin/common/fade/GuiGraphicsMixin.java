package page.langeweile.ok_zoomer.mixin.common.fade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import page.langeweile.ok_zoomer.utils.ZoomUtils;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsMixin {
	@WrapMethod(method = "innerBlit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lcom/mojang/renderpearl/api/textures/GpuTextureView;Lcom/mojang/renderpearl/api/textures/GpuSampler;IIIIFFFFI)V")
	private void modifyFade(RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, Operation<Void> original) {
		if (ZoomUtils.getFadeModifier() != null) {
			float fade = ZoomUtils.getFadeModifier();
			var colorTargetState = pipeline.getColorTargetStates().getFirst();
			if (colorTargetState != null && colorTargetState.blendFunction().isPresent() && colorTargetState.blendFunction().get().alpha().destFactor() == BlendFactor.ZERO) {
				original.call(pipeline, textureView, sampler, x0, y0, x1, y1, u0, u1, v0, v1, ARGB.scaleRGB(color, fade));
			} else {
				original.call(pipeline, textureView, sampler, x0, y0, x1, y1, u0, u1, v0, v1, ARGB.color(fade, color));
			}
		} else {
			original.call(pipeline, textureView, sampler, x0, y0, x1, y1, u0, u1, v0, v1, color);
		}
	}
}

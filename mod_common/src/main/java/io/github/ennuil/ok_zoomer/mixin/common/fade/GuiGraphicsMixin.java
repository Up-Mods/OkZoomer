package io.github.ennuil.ok_zoomer.mixin.common.fade;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@WrapMethod(method = "submitBlit")
	private void modifyFade(RenderPipeline pipeline, GpuTextureView atlasTexture, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color, Operation<Void> original) {
		if (ZoomUtils.getFadeModifier() != null) {
			original.call(pipeline, atlasTexture, x0, y0, x1, y1, u0, u1, v0, v1, ARGB.scaleRGB(color, ZoomUtils.getFadeModifier()));
		} else {
			original.call(pipeline, atlasTexture, x0, y0, x1, y1, u0, u1, v0, v1, color);
		}
	}
}

package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {
	@WrapMethod(method = "getViewDistance")
	private int modifyBlockEntityViewDistance(Operation<Integer> original) {
		if (!ZoomUtils.canSeeDistantEntities()) {
			return original.call();
		} else {
			return original.call() * (Zoom.isZooming() ? Math.max(1, Mth.ceil(Zoom.getZoomDivisor())) : 1);
		}
	}
}

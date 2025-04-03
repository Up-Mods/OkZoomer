package io.github.ennuil.ok_zoomer.mixin.forge;

import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {
	/**
	 * @author Ennui Langeweile
	 * @reason This mixin usually requires Fabric Mixin, but that is incompatible with Forge!
	 */
	@Overwrite
	default int getViewDistance() {
		if (!ZoomUtils.canSeeDistantEntities()) {
			return 64;
		} else {
			return 64 * (Zoom.isZooming() ? Math.max(1, Mth.ceil(Zoom.getZoomDivisor())) : 1);
		}
	}
}

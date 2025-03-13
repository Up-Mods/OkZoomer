package io.github.ennuil.ok_zoomer.mixin.common.distance;

import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@ModifyArg(
		method = "collectVisibleEntities",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;setViewScale(D)V"
		)
	)
	private double modifyViewScale(double original) {
		if (!ZoomUtils.canSeeDistantEntities()) {
			return original;
		} else {
			return original * Math.max(1.0, Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0);
		}
	}
}

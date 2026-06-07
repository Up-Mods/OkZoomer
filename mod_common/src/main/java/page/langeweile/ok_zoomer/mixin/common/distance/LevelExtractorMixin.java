package page.langeweile.ok_zoomer.mixin.common.distance;

import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {
	@ModifyArg(
		method = "extractVisibleEntities",
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

package page.langeweile.ok_zoomer.mixin.common.distance;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin {
	@WrapMethod(method = "getViewDistance")
	private int modifyBlockEntityViewDistance(Operation<Integer> original) {
		if (!ZoomUtils.canSeeDistantEntities() || !Zoom.isTransitionActive()) {
			return original.call();
		} else {
			return original.call() * Mth.ceil(1.0 / Zoom.getTransitionMode().getInternalMultiplier());
		}
	}
}

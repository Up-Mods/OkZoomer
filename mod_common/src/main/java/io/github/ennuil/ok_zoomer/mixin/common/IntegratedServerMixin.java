package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
	@WrapMethod(method = "getScaledTrackingDistance")
	private int modifyEntityViewDistance(int trackingDistance, Operation<Integer> original) {
		if (!ZoomUtils.canSeeDistantEntities()) {
			return original.call(trackingDistance);
		} else {
			return original.call(trackingDistance * (Zoom.isZooming() ? Math.max(1, Mth.ceil(Zoom.getZoomDivisor())) : 1));
		}
	}
}

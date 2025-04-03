package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Method that is compatible with Sodium (without LevelRenderer changes)
@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "setViewScale", at = @At("HEAD"))
	private static void modifyViewScale(double renderDistWeight, CallbackInfo ci, @Local(argsOnly = true) LocalDoubleRef renderDistWeightRef) {
		if (ZoomUtils.canSeeDistantEntities()) {
			renderDistWeightRef.set(renderDistWeight * Math.max(1.0, Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0));
		}
	}
}

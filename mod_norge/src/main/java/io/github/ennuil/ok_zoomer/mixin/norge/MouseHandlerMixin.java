package io.github.ennuil.ok_zoomer.mixin.norge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@WrapOperation(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
		)
	)
	private void applyZoomChanges(LocalPlayer instance, double x, double y, Operation<Void> original, double movementTime, @Local(ordinal = 3) double cursorSpeed){
		if (Zoom.isModifierActive()) {
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			original.call(instance,
				Zoom.getMouseModifier().applyXModifier(x, cursorSpeed, movementTime, zoomDivisor, transitionDivisor),
				Zoom.getMouseModifier().applyYModifier(y, cursorSpeed, movementTime, zoomDivisor, transitionDivisor)
			);
		} else {
			original.call(instance, x, y);
		}
	}
}

package io.github.ennuil.ok_zoomer.mixin.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

	@Unique
	private static final ThreadLocal<Double> CURSOR_SENSITIVITY = new ThreadLocal<>();

	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/Options;smoothCamera:Z"
		)
	)
	private void captureMouseSensitivity(double movementTime, CallbackInfo ci, @Local(ordinal = 3) double f) {
		CURSOR_SENSITIVITY.set(f);
	}

	@WrapOperation(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
		)
	)
	private void applyZoomChanges(LocalPlayer instance, double x, double y, Operation<Void> original, double movementTime){
		if (Zoom.isModifierActive()) {
			double f = CURSOR_SENSITIVITY.get();
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			original.call(instance,
				Zoom.getMouseModifier().applyXModifier(x, f, movementTime, zoomDivisor, transitionDivisor),
				Zoom.getMouseModifier().applyYModifier(y, f, movementTime, zoomDivisor, transitionDivisor)
			);
		}else{
			original.call(instance, x, y);
		}
		CURSOR_SENSITIVITY.remove();
	}
}

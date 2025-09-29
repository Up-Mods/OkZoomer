package io.github.ennuil.ok_zoomer.mixin.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.MouseHandler;
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

	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
		)
	)
	private void applyZoomChanges(double movementTime, CallbackInfo ci, @Local(ordinal = 1) LocalDoubleRef i, @Local(ordinal = 2) LocalDoubleRef j) {
		if (Zoom.isModifierActive()) {
			double f = CURSOR_SENSITIVITY.get();
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			i.set(Zoom.getMouseModifier().applyXModifier(i.get(), f, movementTime, zoomDivisor, transitionDivisor));
			j.set(Zoom.getMouseModifier().applyYModifier(j.get(), f, movementTime, zoomDivisor, transitionDivisor));
		}
		CURSOR_SENSITIVITY.remove();
	}
}

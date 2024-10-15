package io.github.ennuil.ok_zoomer.mixin.sinytra;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Options;invertYMouse()Lnet/minecraft/client/OptionInstance;"
		)
	)
	public void applyZoomChanges(CallbackInfo ci, @Local(argsOnly = true) double movementTime, @Local(ordinal = 4) LocalDoubleRef j, @Local(ordinal = 5) LocalDoubleRef k, @Local(ordinal = 1) double f) {
		if (Zoom.isModifierActive()) {
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			j.set(Zoom.getMouseModifier().applyXModifier(j.get(), f, movementTime, zoomDivisor, transitionDivisor));
			k.set(Zoom.getMouseModifier().applyYModifier(k.get(), f, movementTime, zoomDivisor, transitionDivisor));
		}
	}
}

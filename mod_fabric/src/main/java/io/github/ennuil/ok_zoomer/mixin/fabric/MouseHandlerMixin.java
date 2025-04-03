package io.github.ennuil.ok_zoomer.mixin.fabric;

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
	public void applyZoomChanges(CallbackInfo ci, @Local(ordinal = 1) double e, @Local(ordinal = 2) LocalDoubleRef k, @Local(ordinal = 3) LocalDoubleRef l, @Local(ordinal = 6) double h) {
		if (Zoom.isModifierActive()) {
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			k.set(Zoom.getMouseModifier().applyXModifier(k.get(), h, e, zoomDivisor, transitionDivisor));
			l.set(Zoom.getMouseModifier().applyYModifier(l.get(), h, e, zoomDivisor, transitionDivisor));
		}
	}
}

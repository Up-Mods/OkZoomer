package io.github.ennuil.ok_zoomer.mixin.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import net.minecraft.client.MouseHandler;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Options;invertYMouse()Lnet/minecraft/client/OptionInstance;"
		),
		require = 1
	)
	public void applyZoomChanges(double movementTime, CallbackInfo ci, @Local(ordinal = 1) LocalDoubleRef j, @Local(ordinal = 2) LocalDoubleRef k, @Local(ordinal = 5) double f) {
		if (Zoom.isModifierActive()) {
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			j.set(Zoom.getMouseModifier().applyXModifier(j.get(), f, movementTime, zoomDivisor, transitionDivisor));
			k.set(Zoom.getMouseModifier().applyYModifier(k.get(), f, movementTime, zoomDivisor, transitionDivisor));
		}
	}
}

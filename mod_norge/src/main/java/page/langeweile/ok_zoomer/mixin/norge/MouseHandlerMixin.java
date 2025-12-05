package page.langeweile.ok_zoomer.mixin.norge;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.zoom.Zoom;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/tutorial/Tutorial;onMouse(DD)V"
		)
	)
	public void applyZoomChanges(double movementTime, CallbackInfo ci, @Local(ordinal = 4) LocalDoubleRef d0, @Local(ordinal = 5) LocalDoubleRef d1, @Local(ordinal = 3) double d4) {
		if (Zoom.isModifierActive()) {
			double zoomDivisor = Zoom.isZooming() ? Zoom.getZoomDivisor() : 1.0;
			double transitionDivisor = Zoom.getTransitionMode().getInternalMultiplier();
			d0.set(Zoom.getMouseModifier().applyXModifier(d0.get(), d4, movementTime, zoomDivisor, transitionDivisor));
			d1.set(Zoom.getMouseModifier().applyYModifier(d1.get(), d4, movementTime, zoomDivisor, transitionDivisor));
		}
	}
}

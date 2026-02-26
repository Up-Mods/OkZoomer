package page.langeweile.ok_zoomer.mixin.forge;

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
			target = "Lnet/minecraft/client/Options;invertYMouse()Lnet/minecraft/client/OptionInstance;"
		)
	)
	public void applyZoomChanges(CallbackInfo ci, @Local(ordinal = 1) double e, @Local(ordinal = 5) LocalDoubleRef k, @Local(ordinal = 6) LocalDoubleRef l, @Local(ordinal = 4) double h) {
		if (Zoom.isModifierActive()) {
			double transitionMultiplier = Zoom.getTransitionMode().getInternalMultiplier();
			k.set(Zoom.getMouseModifier().applyXModifier(k.get(), h, e, transitionMultiplier));
			l.set(Zoom.getMouseModifier().applyYModifier(l.get(), h, e, transitionMultiplier));
		}
	}
}

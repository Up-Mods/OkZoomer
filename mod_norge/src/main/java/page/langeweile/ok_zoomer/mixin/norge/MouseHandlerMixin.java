package page.langeweile.ok_zoomer.mixin.norge;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.MouseHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.zoom.Zoom;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
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
		var modifier = Zoom.getZoomCore().mouseModifier();
		if (modifier != null && modifier.getActive()) {
			double transitionMultiplier = Zoom.getZoomCore().transitionMode().getInternalMultiplier()
				* OkZoomerConfigManager.CONFIG.controls.sensitivityScale.value();
			d0.set(modifier.applyXModifier(d0.get(), d4, movementTime, transitionMultiplier));
			d1.set(modifier.applyYModifier(d1.get(), d4, movementTime, transitionMultiplier));
		}
	}
}

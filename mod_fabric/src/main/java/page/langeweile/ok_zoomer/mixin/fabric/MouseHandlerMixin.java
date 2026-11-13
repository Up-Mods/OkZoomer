package page.langeweile.ok_zoomer.mixin.fabric;

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
			value = "FIELD",
			target = "Lnet/minecraft/client/Options;smoothCamera:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private void captureMouseSensitivity(double movementTime, CallbackInfo ci, @Local(ordinal = 3) double f, @Share("cursorSensitivity") LocalDoubleRef sens) {
		sens.set(f);
	}

	@Inject(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/tutorial/Tutorial;onMouse(DD)V"
		)
	)
	public void applyZoomChanges(double movementTime, CallbackInfo ci, @Local(ordinal = 1) LocalDoubleRef i, @Local(ordinal = 2) LocalDoubleRef j, @Share("cursorSensitivity") LocalDoubleRef sens) {
		var modifier = Zoom.getZoomCore().mouseModifier();
		if (modifier != null && modifier.getActive()) {
			double transitionMultiplier = Zoom.getZoomCore().transitionMode().getInternalMultiplier()
				* OkZoomerConfigManager.CONFIG.controls.sensitivityScale.value();
			i.set(modifier.applyXModifier(i.get(), sens.get(), movementTime, transitionMultiplier));
			j.set(modifier.applyYModifier(j.get(), sens.get(), movementTime, transitionMultiplier));
		}
	}
}

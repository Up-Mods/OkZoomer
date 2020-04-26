package io.github.joaoh1.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;

//TODO - reimplement Reduce Sensitivity option
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 2, shift = At.Shift.AFTER), method = "updateMouse()V", locals = LocalCapture.CAPTURE_FAILHARD)
	private void reduceMouseSensitivity(CallbackInfo info, double e, double f, double g) {
        if (OkZoomerConfig.reduceSensitivity.getValue()) {
            double reducedMouseSensitivity = this.client.options.mouseSensitivity / 1000.0D;
            f = reducedMouseSensitivity * 0.6000000238418579D + 0.20000000298023224D;
            g = f * f * f * 8.0D;
        }
	}
}
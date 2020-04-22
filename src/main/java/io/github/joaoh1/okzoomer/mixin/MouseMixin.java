package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.Mouse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.OkZoomerMod;
import io.github.joaoh1.okzoomer.config.OkZoomerConfig;

@Mixin(Mouse.class)
public class MouseMixin {
   @Shadow
   private double eventDeltaWheel;
   
   @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), method = "onMouseScroll(JDD)V", cancellable = true)
   private void zoomerOnMouseScroll(CallbackInfo info) {
      if (OkZoomerMod.shouldScrollZoom) {
         double scrollAmount = OkZoomerMod.scrollZoom(OkZoomerConfig.zoomDivisor.getValue(), this.eventDeltaWheel);
         OkZoomerConfig.zoomDivisor.setValue(scrollAmount);
         
         this.eventDeltaWheel = 0.0D;
         info.cancel();
      }
   }
}

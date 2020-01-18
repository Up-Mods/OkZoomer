package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.Mouse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.OkZoomer;

@Mixin(Mouse.class)
public class MouseMixin {
   @Shadow
   private double eventDeltaWheel;

   @Inject(method = "onMouseScroll(JDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isSpectator()Z", shift = At.Shift.BEFORE), cancellable = true)
   private void onMouseScroll(CallbackInfo info) {
      if (OkZoomer.shouldScrollZoom()) {
         double scrollAmount = (double)((float)(int)(this.eventDeltaWheel));
         OkZoomer.scrollZoom(scrollAmount);
         info.cancel();
      }
   }
}
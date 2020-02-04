package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.Mouse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.OkZoomer;
import io.github.joaoh1.okzoomer.OkZoomerConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

@Mixin(Mouse.class)
public class MouseMixin {
   @Shadow
   private double eventDeltaWheel;

   OkZoomerConfig config = AutoConfig.getConfigHolder(OkZoomerConfig.class).getConfig();
   
   @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), method = "onMouseScroll(JDD)V", cancellable = true)
   private void onMouseScroll(CallbackInfo info) {
      if (OkZoomer.shouldScrollZoom()) {
         double scrollAmount = OkZoomer.scrollZoom(config.zoomDivisor, this.eventDeltaWheel, config.minimumZoomDivisor, config.maximumZoomDivisor);
         config.zoomDivisor = scrollAmount;
         
         this.eventDeltaWheel = (double)0.0;
         info.cancel();
      }
   }
}

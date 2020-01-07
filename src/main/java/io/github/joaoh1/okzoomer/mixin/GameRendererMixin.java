package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.joaoh1.okzoomer.OkZoomer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
	private boolean renderHand;

    @Inject(at = @At("HEAD"), method = "net/minecraft/client/render/GameRenderer.tick()V")
	private void renderWorld(CallbackInfo info) {
        if (OkZoomer.shouldHideHands()) {
            this.renderHand = false;
        } else {
            this.renderHand = true;
        }
    }
}
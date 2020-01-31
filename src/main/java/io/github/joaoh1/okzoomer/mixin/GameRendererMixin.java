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

    //This is injected in the tick method to prevent any conflicts with other mods.
    @Inject(at = @At("HEAD"), method = "net/minecraft/client/render/GameRenderer.tick()V")
	private void zoomerRenderWorld(CallbackInfo info) {
        if (OkZoomer.shouldHideHands()) {
            this.renderHand = false;
        } else {
            this.renderHand = true;
        }
    }

    @Shadow
    private float movementFovMultiplier;

    private float zoomedMovementFovMultiplier = 1.0f;

    @Inject(at = @At("TAIL"), method = "net/minecraft/client/render/GameRenderer.updateMovementFovMultiplier()V")
    private void zoomerUpdateMovementFovMultiplier(CallbackInfo info) {
        if (OkZoomer.shouldZoomSmoothly()) {
            this.movementFovMultiplier = (float)(OkZoomer.getZoomMultiplier()) * zoomedMovementFovMultiplier;
        } else {
            zoomedMovementFovMultiplier = this.movementFovMultiplier;
        }
    }
}
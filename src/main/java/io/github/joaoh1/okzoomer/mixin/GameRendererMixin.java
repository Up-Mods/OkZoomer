package io.github.joaoh1.okzoomer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.joaoh1.okzoomer.OkZoomerConfig;
import io.github.joaoh1.okzoomer.OkZoomerMod;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    private final MinecraftClient minecraft = MinecraftClient.getInstance();

    @Shadow
	private boolean renderHand;

    //This is injected in the tick method to prevent any conflicts with other mods.
    @Inject(at = @At("HEAD"), method = "net/minecraft/client/render/GameRenderer.tick()V")
	private void zoomerRenderWorld(CallbackInfo info) {
        if (OkZoomerMod.shouldHideHands) {
            this.renderHand = false;
        } else {
            this.renderHand = true;
        }
    }

    @Shadow
    private float movementFovMultiplier;

    private float zoomedMovementFovMultiplier = 1.0f;

    OkZoomerConfig config = AutoConfig.getConfigHolder(OkZoomerConfig.class).getConfig();

    @Inject(at = @At("TAIL"), method = "net/minecraft/client/render/GameRenderer.updateMovementFovMultiplier()V")
    private void zoomerUpdateMovementFovMultiplier(CallbackInfo info) {
        if (OkZoomerMod.shouldZoomSmoothly) {
            if (this.movementFovMultiplier != (float)(1.0 / config.zoomDivisor) * zoomedMovementFovMultiplier) {
                this.movementFovMultiplier = (float)(1.0 / config.zoomDivisor) * zoomedMovementFovMultiplier;
            }
        } else {
            zoomedMovementFovMultiplier = this.movementFovMultiplier;
        }
    }

    @Inject(at = @At("TAIL"), method = "net/minecraft/client/render/GameRenderer.getFov(Lnet/minecraft/client/render/Camera;FZ)D")
    private double zoomerGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> info) {
        if (changingFov) {
            minecraft.worldRenderer.scheduleTerrainUpdate();
        }

        return info.getReturnValueD();
    }
}
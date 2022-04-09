package io.github.ennuil.okzoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyExpressionValue(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"),
        method = "render"
    )
    private boolean replaceSpyglassMouseMovement(boolean isUsingSpyglass) {
        if (switch (ZoomPackets.getSpyglassDependency()) {
            case REPLACE_ZOOM -> true;
            case BOTH -> true;
            default -> false;
        }) {
            return false;
        } else {
            return isUsingSpyglass;
        }
    }
}

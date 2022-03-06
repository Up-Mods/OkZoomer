package io.github.ennuil.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Redirect(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"),
        method = "render"
    )
    private boolean replaceSpyglassMouseMovement(ClientPlayerEntity player) {
        if (switch (OkZoomerConfigManager.configInstance.features().getSpyglassDependency()) {
            case REPLACE_ZOOM -> true;
            case BOTH -> true;
            default -> false;
        }) {
            return false;
        } else {
            return player.isUsingSpyglass();
        }
    }
}

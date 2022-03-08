package io.github.ennuil.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.minecraft.client.network.AbstractClientPlayerEntity;

// TODO - I hate Redirects; How am I going to get rid of it?
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
    @Redirect(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z"),
        method = "getSpeed"
    )
    private boolean replaceSpyglassMouseMovement(AbstractClientPlayerEntity player) {
        if (switch (ZoomPackets.getSpyglassDependency()) {
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

package io.github.ennuil.okzoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
	@ModifyExpressionValue(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z"),
		method = "getSpeed"
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

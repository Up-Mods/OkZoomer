package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {
	@ModifyExpressionValue(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z"),
		method = "getSpeed"
	)
	private boolean replaceSpyglassMouseMovement(boolean isUsingSpyglass) {
		if (switch (OkZoomerConfigManager.SPYGLASS_DEPENDENCY.value()) {
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

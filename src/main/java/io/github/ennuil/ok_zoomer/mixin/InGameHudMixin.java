package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.gui.hud.in_game.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@ModifyExpressionValue(
		method = "render",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z")
	)
	private boolean activateSpyglassOverlay(boolean isUsingSpyglass) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglass_dependency.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isUsingSpyglass;
	}
}

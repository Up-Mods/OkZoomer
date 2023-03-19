package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@ModifyExpressionValue(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"),
		method = "render"
	)
	private boolean ok_zoomer$activateSpyglassOverlay(boolean isUsingSpyglass) {
		if (switch (OkZoomerConfigManager.SPYGLASS_DEPENDENCY.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isUsingSpyglass;
	}
}

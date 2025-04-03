package io.github.ennuil.ok_zoomer.mixin.forge;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {
	@ModifyExpressionValue(
		method = "renderSpyglassOverlay",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z")
	)
	private boolean activateSpyglassOverlay(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}

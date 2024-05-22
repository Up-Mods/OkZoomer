package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.gui.Gui;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(Gui.class)
public abstract class GuiMixin {
	@ModifyExpressionValue(
		method = "renderCameraOverlays",
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

package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	// Handles zoom scrolling
	@Inject(
		method = "onScroll",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
		cancellable = true
	)
	private void zoomerOnMouseScroll(CallbackInfo ci, @Local(ordinal = 2) int k) {
		if (k != 0) {
			if (OkZoomerConfigManager.CONFIG.features.zoom_scrolling.value()) {
				if (OkZoomerConfigManager.CONFIG.features.zoom_mode.value().equals(ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isDown()) return;
				}

				if (ZoomUtils.ZOOMER_ZOOM.getZoom()) {
					ZoomUtils.changeZoomDivisor(k > 0);
					ci.cancel();
				}
			}
		}
	}

	// Handles the zoom scrolling reset through the middle button
	@Inject(
		method = "onPress",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"),
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void zoomerOnMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci, boolean bl, int i) {
		if (OkZoomerConfigManager.CONFIG.features.zoom_scrolling.value()) {
			if (OkZoomerConfigManager.CONFIG.features.zoom_mode.value() == ZoomModes.PERSISTENT && !ZoomKeyBinds.ZOOM_KEY.isDown()) {
				return;
			}

			if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && bl && ZoomKeyBinds.ZOOM_KEY.isDown()) {
				if (OkZoomerConfigManager.CONFIG.tweaks.reset_zoom_with_mouse.value()) {
					ZoomUtils.resetZoomDivisor(true);
					ci.cancel();
				}
			}
		}
	}

	// Prevents the spyglass from working if zooming replaces its zoom
	@ModifyExpressionValue(
		method = "turnPlayer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z")
	)
	private boolean replaceSpyglassMouseMovement(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglass_dependency.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}

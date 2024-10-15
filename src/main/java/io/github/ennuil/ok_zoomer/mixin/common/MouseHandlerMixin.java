package io.github.ennuil.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.ennuil.ok_zoomer.zoom.Zoom;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
@ClientOnly
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
			if (OkZoomerConfigManager.CONFIG.features.zoomScrolling.value()) {
				if (OkZoomerConfigManager.CONFIG.features.zoomMode.value().equals(ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isDown()) return;
				}

				if (Zoom.isZooming()) {
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
		cancellable = true
	)
	private void zoomerOnMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci, @Local boolean bl, @Local(ordinal = 3) int i) {
		if (OkZoomerConfigManager.CONFIG.features.zoomScrolling.value()) {
			if (OkZoomerConfigManager.CONFIG.features.zoomMode.value() == ZoomModes.PERSISTENT && !ZoomKeyBinds.ZOOM_KEY.isDown()) {
				return;
			}

			if (i == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && bl && ZoomKeyBinds.ZOOM_KEY.isDown()) {
				if (OkZoomerConfigManager.CONFIG.tweaks.resetZoomWithMouse.value()) {
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
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}

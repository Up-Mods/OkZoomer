package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Mouse;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
@Mixin(Mouse.class)
public abstract class MouseMixin {
	// Handles zoom scrolling
	@Inject(
		method = "onMouseScroll",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"),
		cancellable = true
	)
	private void zoomerOnMouseScroll(CallbackInfo ci, @Local(ordinal = 2) int k) {
		if (k != 0) {
			if (OkZoomerConfigManager.CONFIG.features.zoom_scrolling.value()) {
				if (OkZoomerConfigManager.CONFIG.features.zoom_mode.value().equals(ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isPressed()) return;
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
		method = "onMouseButton",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBind;setKeyPressed(Lcom/mojang/blaze3d/platform/InputUtil$Key;Z)V"),
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void zoomerOnMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci, boolean bl, int i) {
		if (OkZoomerConfigManager.CONFIG.features.zoom_scrolling.value()) {
			if (OkZoomerConfigManager.CONFIG.features.zoom_mode.value().equals(ZoomModes.PERSISTENT)) {
				if (!ZoomKeyBinds.ZOOM_KEY.isPressed()) return;
			}

			if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && bl) {
				if (ZoomKeyBinds.ZOOM_KEY.isPressed()) {
					if (OkZoomerConfigManager.CONFIG.tweaks.reset_zoom_with_mouse.value()) {
						ZoomUtils.resetZoomDivisor(true);
						ci.cancel();
					}
				}
			}
		}
	}

	// Prevents the spyglass from working if zooming replaces its zoom
	@ModifyExpressionValue(
		method = "updateLookDirection",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z")
	)
	private boolean replaceSpyglassMouseMovement(boolean isUsingSpyglass) {
		if (switch (OkZoomerConfigManager.CONFIG.features.spyglass_dependency.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isUsingSpyglass;
	}
}

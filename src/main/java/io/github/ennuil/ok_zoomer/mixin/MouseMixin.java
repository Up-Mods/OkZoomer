package io.github.ennuil.ok_zoomer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

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
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.Mouse;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
@Mixin(Mouse.class)
public abstract class MouseMixin {
	@Shadow
	private double scrollDelta;

	// Handles zoom scrolling
	@Inject(
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;scrollDelta:D", ordinal = 7),
		method = "onMouseScroll",
		cancellable = true
	)
	private void zoomerOnMouseScroll(CallbackInfo info) {
		if (this.scrollDelta != 0.0) {
			if (OkZoomerConfigManager.ZOOM_SCROLLING.value() && !ZoomPackets.getDisableZoomScrolling()) {
				if (OkZoomerConfigManager.ZOOM_MODE.value().equals(ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isPressed()) return;
				}

				if (ZoomUtils.ZOOMER_ZOOM.getZoom()) {
					ZoomUtils.changeZoomDivisor(this.scrollDelta > 0.0);
					info.cancel();
				}
			}
		}
	}

	// Handles the zoom scrolling reset through the middle button
	@Inject(
		at = @At(value = "INVOKE", target = "net/minecraft/client/option/KeyBind.setKeyPressed(Lcom/mojang/blaze3d/platform/InputUtil$Key;Z)V"),
		method = "onMouseButton",
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void zoomerOnMouseButton(long window, int button, int action, int modifiers, CallbackInfo info, boolean bl, int i) {
		if (OkZoomerConfigManager.ZOOM_SCROLLING.value() && !ZoomPackets.getDisableZoomScrolling()) {
			if (OkZoomerConfigManager.ZOOM_MODE.value().equals(ZoomModes.PERSISTENT)) {
				if (!ZoomKeyBinds.ZOOM_KEY.isPressed()) return;
			}

			if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && bl) {
				if (ZoomKeyBinds.ZOOM_KEY.isPressed()) {
					if (OkZoomerConfigManager.RESET_ZOOM_WITH_MOUSE.value()) {
						ZoomUtils.resetZoomDivisor(true);
						info.cancel();
					}
				}
			}
		}
	}

	// Prevents the spyglass from working if zooming replaces its zoom
	@ModifyExpressionValue(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingSpyglass()Z"),
		method = "updateLookDirection"
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

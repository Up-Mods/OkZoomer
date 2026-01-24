package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import page.langeweile.ok_zoomer.config.ConfigEnums;
import page.langeweile.ok_zoomer.config.ConfigEnums.ZoomModes;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.key_binds.ZoomKeyBinds;
import page.langeweile.ok_zoomer.utils.ZoomUtils;
import page.langeweile.ok_zoomer.zoom.Zoom;

// This mixin is responsible for the mouse-behavior-changing part of the zoom
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Inject(
		method = "onScroll",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
		),
		cancellable = true
	)
	private void zoomScrollOnScroll(CallbackInfo ci, @Local(ordinal = 2) int i) {
		if (i != 0) {
			if (OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling.value()) {
				if (OkZoomerConfigManager.CONFIG.controls.zoomMode.value().equals(ConfigEnums.ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isDown()) return;
				}

				if (Zoom.isZooming()) {
					ZoomUtils.changeZoomDivisor(i > 0);
					ci.cancel();
				}
			}
		}
	}

	// Handles the zoom scrolling reset through the middle button
	@Inject(
		method = "onPress",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"
		),
		cancellable = true
	)
	private void zoomerOnMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
		if (OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling.value()) {
			if (OkZoomerConfigManager.CONFIG.controls.zoomMode.value() == ZoomModes.PERSISTENT && !ZoomKeyBinds.ZOOM_KEY.isDown()) {
				return;
			}

			if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW.GLFW_PRESS && Zoom.isZooming()) {
				if (OkZoomerConfigManager.CONFIG.zoomScrolling.resetZoomWithMouse.value()) {
					ZoomUtils.resetZoomDivisor(true);
					ci.cancel();
				}
			}
		}
	}

	// Prevents the spyglass from working if zooming replaces its zoom
	@ModifyExpressionValue(
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isScoping()Z"
		)
	)
	private boolean replaceSpyglassMouseMovement(boolean isScoping) {
		if (switch (OkZoomerConfigManager.CONFIG.controls.spyglassMode.value()) {
			case REPLACE_ZOOM, BOTH -> true;
			default -> false;
		}) {
			return false;
		}

		return isScoping;
	}
}

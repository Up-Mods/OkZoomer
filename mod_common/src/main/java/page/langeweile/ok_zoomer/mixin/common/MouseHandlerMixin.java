package page.langeweile.ok_zoomer.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
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
		method = "turnPlayer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/tutorial/Tutorial;onMouse(DD)V"
		)
	)
	public void applyZoomChanges(double movementTime, CallbackInfo ci, @Local(name = "xo") LocalDoubleRef xo, @Local(name = "yo") LocalDoubleRef yo, @Local(name = "sens") double sens) {
		if (Zoom.isModifierActive()) {
			double transitionMultiplier = Zoom.getTransitionMode().getInternalMultiplier();
			transitionMultiplier *= OkZoomerConfigManager.CONFIG.controls.sensitivityScale.value();
			xo.set(Zoom.getMouseModifier().applyXModifier(xo.get(), sens, movementTime, transitionMultiplier));
			yo.set(Zoom.getMouseModifier().applyYModifier(yo.get(), sens, movementTime, transitionMultiplier));
		}
	}

	@Inject(
		method = "onScroll",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
		),
		cancellable = true
	)
	private void zoomScrollOnScroll(CallbackInfo ci, @Local int wheel) {
		if (wheel != 0) {
			if (OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling.value()) {
				if (OkZoomerConfigManager.CONFIG.controls.zoomMode.value().equals(ConfigEnums.ZoomModes.PERSISTENT)) {
					if (!ZoomKeyBinds.ZOOM_KEY.isDown()) return;
				}

				if (Zoom.isZooming()) {
					ZoomUtils.changeZoomDivisor(wheel > 0);
					ci.cancel();
				}
			}
		}
	}

	// Handles the zoom scrolling reset through the middle button
	@Inject(
		method = "onButton",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"
		),
		cancellable = true
	)
	private void zoomerOnMouseButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
		if (OkZoomerConfigManager.CONFIG.zoomScrolling.zoomScrolling.value()) {
			if (OkZoomerConfigManager.CONFIG.controls.zoomMode.value() == ZoomModes.PERSISTENT && !ZoomKeyBinds.ZOOM_KEY.isDown()) {
				return;
			}

			if (rawButtonInfo.button() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW.GLFW_PRESS && Zoom.isZooming()) {
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

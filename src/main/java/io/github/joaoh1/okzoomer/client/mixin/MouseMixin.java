package io.github.joaoh1.okzoomer.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.joaoh1.okzoomer.client.keybinds.ZoomKeybinds;
import io.github.joaoh1.okzoomer.client.packets.ZoomPackets;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.SmoothUtil;

//This mixin is responsible for the mouse-behavior-changing part of the zoom.
@Mixin(Mouse.class)
public class MouseMixin {
	@Final
	@Shadow
	private MinecraftClient client;
	
	@Shadow
	private final SmoothUtil cursorXSmoother = new SmoothUtil();
	
	@Shadow
	private final SmoothUtil cursorYSmoother = new SmoothUtil();

	@Shadow
	private double cursorDeltaX;

	@Shadow
	private double cursorDeltaY;
	
	@Shadow
	private double eventDeltaWheel;
	
	private final SmoothUtil cursorXZoomSmoother = new SmoothUtil();
	private final SmoothUtil cursorYZoomSmoother = new SmoothUtil();

	private double extractedE;
	private double adjustedG;
	
	//This mixin handles the "Reduce Sensitivity" option and extracts the g variable for the cinematic cameras.
	@ModifyVariable(
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 2),
		method = "updateMouse()V", 
		ordinal = 2
	)
	private double applyReduceSensitivity(double g) {
		double modifiedMouseSensitivity = this.client.options.mouseSensitivity;
		if (OkZoomerConfigPojo.features.reduceSensitivity) {
			if (!OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.OFF)) {
				modifiedMouseSensitivity *= ZoomUtils.zoomFovMultiplier;	
			} else if (ZoomUtils.zoomState) {
				modifiedMouseSensitivity /= ZoomUtils.zoomDivisor;
			}
		}
		double appliedMouseSensitivity = modifiedMouseSensitivity * 0.6 + 0.2;
		g = appliedMouseSensitivity * appliedMouseSensitivity * appliedMouseSensitivity * 8.0;
		this.adjustedG = g;
		return g;
	}
	
	//Extracts the e variable for the cinematic cameras.
	@Inject(
		at = @At(value = "INVOKE", target = "net/minecraft/client/Mouse.isCursorLocked()Z"),
		method = "updateMouse()V",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void obtainCinematicCameraValues(CallbackInfo info, double d, double e) {
		this.extractedE = e;
	}

	//Applies the cinematic camera on the mouse's X.
	@ModifyVariable(
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;cursorDeltaX:D", ordinal = 3, shift = At.Shift.BEFORE),
		method = "updateMouse()V",
		ordinal = 1
	)
	private double applyCinematicModeX(double l) {
		if (!OkZoomerConfigPojo.features.cinematicCamera.equals(CinematicCameraOptions.OFF)) {
			if (ZoomUtils.zoomState) {
				if (this.client.options.smoothCameraEnabled) {
					l = this.cursorXSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG));
					this.cursorXZoomSmoother.clear();
				} else {
					l = this.cursorXZoomSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG));
				}
				if (OkZoomerConfigPojo.features.cinematicCamera.equals(CinematicCameraOptions.MULTIPLIED)) {
					l *= OkZoomerConfigPojo.values.cinematicMultiplier;
				}
			} else {
				this.cursorXZoomSmoother.clear();
			}
		}
		
		return l;
	}
	
	//Applies the cinematic camera on the mouse's Y.
	@ModifyVariable(
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;cursorDeltaY:D", ordinal = 3, shift = At.Shift.BEFORE),
		method = "updateMouse()V",
		ordinal = 2
	)
	private double applyCinematicModeY(double m) {
		if (!OkZoomerConfigPojo.features.cinematicCamera.equals(CinematicCameraOptions.OFF)) {
			if (ZoomUtils.zoomState) {
				if (this.client.options.smoothCameraEnabled) {
					m = this.cursorYSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG));
					this.cursorYZoomSmoother.clear();
				} else {
					m = this.cursorYZoomSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG));
				}
				if (OkZoomerConfigPojo.features.cinematicCamera.equals(CinematicCameraOptions.MULTIPLIED)) {
					m *= OkZoomerConfigPojo.values.cinematicMultiplier;
				}
			} else {
				this.cursorYZoomSmoother.clear();
			}
		}
		
		return m;
	}
	
	//Handles zoom scrolling.
	@Inject(
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7),
		method = "onMouseScroll(JDD)V",
		cancellable = true
	)
	private void zoomerOnMouseScroll(CallbackInfo info) {
		if (this.eventDeltaWheel != 0.0) {
			if (OkZoomerConfigPojo.features.zoomScrolling && !ZoomPackets.disableZoomScrolling) {
				if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.PERSISTENT)) {
					if (!ZoomKeybinds.zoomKey.isPressed()) {
						return;
					}
				}
				
				if (ZoomUtils.zoomState) {
					if (this.eventDeltaWheel > 0.0) {
						ZoomUtils.changeZoomDivisor(true);
					} else if (this.eventDeltaWheel < 0.0) {
						ZoomUtils.changeZoomDivisor(false);
					}
					
					info.cancel();
				}
			}
		}
	}

	//Handles the zoom scrolling reset through the middle button.
	@Inject(
		at = @At(value = "INVOKE", target = "net/minecraft/client/options/KeyBinding.setKeyPressed(Lnet/minecraft/client/util/InputUtil$KeyCode;Z)V"),
		method = "onMouseButton(JIII)V",
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void zoomerOnMouseButton(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i) {
		if (OkZoomerConfigPojo.features.zoomScrolling && !ZoomPackets.disableZoomScrolling) {
			if (OkZoomerConfigPojo.features.zoomMode.equals(ZoomModes.PERSISTENT)) {
				if (!ZoomKeybinds.zoomKey.isPressed()) {
					return;
				}
			}
	
			if (button == 2 && bl == true) {
				if (ZoomKeybinds.zoomKey.isPressed()) {
					if (OkZoomerConfigPojo.tweaks.resetZoomWithMouse) {
						ZoomUtils.resetZoomDivisor();
						info.cancel();
					}
				}
			}
		}
	}
}
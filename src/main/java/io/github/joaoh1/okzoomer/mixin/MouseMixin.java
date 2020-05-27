package io.github.joaoh1.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.joaoh1.okzoomer.OkZoomerMod;
import io.github.joaoh1.okzoomer.config.OkZoomerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.SmoothUtil;

//TODO - Comment this code better
//This mixin is responsible for the mouse-behavior-changing part of the zoom.
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private final MinecraftClient client;

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

    public MouseMixin(MinecraftClient client) {
        this.client = client;
    }
    
    //This mixin handles the "Reduce Sensitivity" option.
    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;client:Lnet/minecraft/client/MinecraftClient;", ordinal = 2), method = "updateMouse()V", ordinal = 2)
	private double applyReduceSensitivity(double g) {
        double modifiedMouseSensitivity = this.client.options.mouseSensitivity;
        if (OkZoomerConfig.reduceSensitivity.getValue()) {
            if (OkZoomerMod.isZoomKeyPressed) {
                modifiedMouseSensitivity /= OkZoomerMod.zoomDivisor;
            }
        }
        double appliedMouseSensitivity = modifiedMouseSensitivity * 0.6000000238418579D + 0.20000000298023224D;
        g = appliedMouseSensitivity * appliedMouseSensitivity * appliedMouseSensitivity * 8.0D;
        this.adjustedG = g;
        return g;
    }
    
    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/client/Mouse.isCursorLocked()Z"), method = "updateMouse()V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void obtainCinematicCameraValues(CallbackInfo info, double d, double e) {
        this.extractedE = e;
    }

    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;cursorDeltaX:D", ordinal = 3, shift = At.Shift.BEFORE), method = "updateMouse()V", ordinal = 1)
	private double applyCinematicModeX(double l) {
        if (!OkZoomerConfig.cinematicCamera.getValue().equals("off") && OkZoomerMod.isZoomKeyPressed) {
            if (OkZoomerConfig.cinematicCamera.getValue().equals("vanilla")) {
                if (this.client.options.smoothCameraEnabled) {
                    l = this.cursorXSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG));
                    this.cursorXZoomSmoother.clear();
                } else {
                    l = this.cursorXZoomSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG));
                }
            } else if (OkZoomerConfig.cinematicCamera.getValue().equals("multiplied")) {
                if (this.client.options.smoothCameraEnabled) {
                    this.cursorXSmoother.smooth(-(this.cursorDeltaX * this.adjustedG), -(this.extractedE * this.adjustedG));
                    l = this.cursorXSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG) * OkZoomerConfig.cinematicMultiplier.getValue());
                } else {
                    l = this.cursorXZoomSmoother.smooth(this.cursorDeltaX * this.adjustedG, (this.extractedE * this.adjustedG) * OkZoomerConfig.cinematicMultiplier.getValue());
                }
            }
        } else {
            this.cursorXZoomSmoother.clear();
        }
        return l;
    }
    
    @ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;cursorDeltaY:D", ordinal = 3, shift = At.Shift.BEFORE), method = "updateMouse()V", ordinal = 2)
	private double applyCinematicModeY(double m) {
        if (!OkZoomerConfig.cinematicCamera.getValue().equals("off") && OkZoomerMod.isZoomKeyPressed) {
            if (OkZoomerConfig.cinematicCamera.getValue().equals("vanilla")) {
                if (this.client.options.smoothCameraEnabled) {
                    m = this.cursorYSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG));
                    this.cursorYZoomSmoother.clear();
                } else {
                    m = this.cursorYZoomSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG));
                }
            } else if (OkZoomerConfig.cinematicCamera.getValue().equals("multiplied")) {
                if (this.client.options.smoothCameraEnabled) {
                    this.cursorYSmoother.smooth(-(this.cursorDeltaY * this.adjustedG), -(this.extractedE * this.adjustedG));
                    m = this.cursorYSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG) * OkZoomerConfig.cinematicMultiplier.getValue());
                } else {
                    m = this.cursorYZoomSmoother.smooth(this.cursorDeltaY * this.adjustedG, (this.extractedE * this.adjustedG) * OkZoomerConfig.cinematicMultiplier.getValue());
                }
            }
        } else {
            this.cursorYZoomSmoother.clear();
        }
        return m;
    }

@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), method = "onMouseScroll(JDD)V", cancellable = true)
    private void zoomerOnMouseScroll(CallbackInfo info) {
        if (OkZoomerConfig.zoomScrolling.getValue()) {
            if (OkZoomerMod.isZoomKeyPressed) {
                if (this.eventDeltaWheel != 0.0) {
                    if (this.eventDeltaWheel > 0.0) {
                        OkZoomerMod.changeZoomDivisor(true);
                    } else if (this.eventDeltaWheel < 0.0) {
                        OkZoomerMod.changeZoomDivisor(false);
                    }

                    info.cancel();
                }
            }
        }
    }
}
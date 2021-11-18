package io.github.ennuil.okzoomer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.keybinds.ZoomKeybinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import net.minecraft.client.Mouse;

// This mixin is responsible for the mouse-behavior-changing part of the zoom.
@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private double eventDeltaWheel;
    
    // Handles zoom scrolling.
    @Inject(
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7),
        method = "onMouseScroll(JDD)V",
        cancellable = true
    )
    private void zoomerOnMouseScroll(CallbackInfo info) {
        if (this.eventDeltaWheel != 0.0) {
            if (OkZoomerConfigManager.INSTANCE.features().zoomScrolling() && !ZoomPackets.getDisableZoomScrolling()) {
                if (OkZoomerConfigManager.INSTANCE.features().zoomMode().equals(ZoomModes.PERSISTENT)) {
                    if (!ZoomKeybinds.zoomKey.isPressed()) return;
                }
                
                if (ZoomUtils.zoomerZoom.getZoom()) {
                    ZoomUtils.changeZoomDivisor(this.eventDeltaWheel > 0.0);
                    info.cancel();
                }
            }
        }
    }

    // Handles the zoom scrolling reset through the middle button.
    @Inject(
        at = @At(value = "INVOKE", target = "net/minecraft/client/option/KeyBinding.setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"),
        method = "onMouseButton(JIII)V",
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void zoomerOnMouseButton(long window, int button, int action, int mods, CallbackInfo info, boolean bl, int i) {
        if (OkZoomerConfigManager.INSTANCE.features().zoomScrolling() && !ZoomPackets.getDisableZoomScrolling()) {
            if (OkZoomerConfigManager.INSTANCE.features().zoomMode().equals(ZoomModes.PERSISTENT)) {
                if (!ZoomKeybinds.zoomKey.isPressed()) return;
            }
            
            if (button == 2 && bl) {
                if (ZoomKeybinds.zoomKey.isPressed()) {
                    if (OkZoomerConfigManager.INSTANCE.tweaks().resetZoomWithMouse()) {
                        ZoomUtils.resetZoomDivisor();
                        info.cancel();
                    }
                }
            }
        }
    }
}
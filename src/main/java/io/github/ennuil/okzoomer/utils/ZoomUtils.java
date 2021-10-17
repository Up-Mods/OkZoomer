package io.github.ennuil.okzoomer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.ennuil.libzoomer.api.ZoomRegistry;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.NoZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo;
import io.github.ennuil.okzoomer.keybinds.ZoomKeybinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

// The class that contains most of the logic behind the zoom itself.
public class ZoomUtils {
    // The logger, used everywhere to print messages to the console.
    public static final Logger modLogger = LogManager.getFormatterLogger("Ok Zoomer");

    public static ZoomInstance zoomerZoom = ZoomRegistry.registerInstance(new ZoomInstance(
            new Identifier("okzoomer:zoom"),
            4.0F,
            new SmoothTransitionMode(0.75f),
            new ZoomDivisorMouseModifier(),
            new NoZoomOverlay()
        ));

    // The zoom FOV multipliers. Used by the GameRenderer mixin.
    public static float zoomFovMultiplier = 1.0F;
    public static float lastZoomFovMultiplier = 1.0F;

    // The zoom overlay's alpha. Used by the InGameHud mixin.
    public static float zoomOverlayAlpha = 0.0F;
    public static float lastZoomOverlayAlpha = 0.0F;

    // The method used for changing the zoom divisor, used by zoom scrolling and the keybinds.
    public static final void changeZoomDivisor(boolean increase) {
        //If the zoom is disabled, don't allow for zoom scrolling
        if (ZoomPackets.getDisableZoom() || ZoomPackets.getDisableZoomScrolling()) {
            return;
        }

        double zoomDivisor = zoomerZoom.getZoomDivisor();
        double minimumZoomDivisor = OkZoomerConfigPojo.values.minimumZoomDivisor;
        double maximumZoomDivisor = OkZoomerConfigPojo.values.maximumZoomDivisor;
        double changedZoomDivisor;
        double lesserChangedZoomDivisor;

        if (ZoomPackets.getForceZoomDivisors()) {
            minimumZoomDivisor = ZoomPackets.getMinimumZoomDivisor();
            maximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
        }

        if (increase) {
            changedZoomDivisor = zoomDivisor + OkZoomerConfigPojo.values.scrollStep;
            lesserChangedZoomDivisor = zoomDivisor + OkZoomerConfigPojo.values.lesserScrollStep;
        } else {
            changedZoomDivisor = zoomDivisor - OkZoomerConfigPojo.values.scrollStep;
            lesserChangedZoomDivisor = zoomDivisor - OkZoomerConfigPojo.values.lesserScrollStep;
        }

        if (lesserChangedZoomDivisor <= zoomerZoom.getDefaultZoomDivisor()) {
            changedZoomDivisor = lesserChangedZoomDivisor;
        }

        if (changedZoomDivisor >= minimumZoomDivisor) {
            if (changedZoomDivisor <= maximumZoomDivisor) {
                zoomerZoom.setZoomDivisor(changedZoomDivisor);
            }
        }
    }

    // The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak.
    public static final void resetZoomDivisor() {
        if (ZoomPackets.getDisableZoom() || ZoomPackets.getDisableZoomScrolling()) {
            return;
        }

        zoomerZoom.resetZoomDivisor();
    }

    // The method used for unbinding the "Save Toolbar Activator"
    public static final void unbindConflictingKey(MinecraftClient client, boolean userPrompted) {
        if (ZoomKeybinds.zoomKey.isDefault()) {
            if (client.options.keySaveToolbarActivator.isDefault()) {
                if (userPrompted) {
                    ZoomUtils.modLogger.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding...");
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.unbind_conflicting_key.success")));
                } else {
                    ZoomUtils.modLogger.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated until specified in the config.");
                }
                client.options.keySaveToolbarActivator.setBoundKey(InputUtil.UNKNOWN_KEY);
                client.options.write();
                KeyBinding.updateKeysByCode();
            } else {
                ZoomUtils.modLogger.info("[Ok Zoomer] No conflicts with the \"Save Toolbar Activator\" keybind were found!");
                if (userPrompted) {
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.unbind_conflicting_key.no_conflict")));
                }
            }
        }
    }
}
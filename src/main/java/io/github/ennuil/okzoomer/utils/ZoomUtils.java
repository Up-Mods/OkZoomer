package io.github.ennuil.okzoomer.utils;

import com.mojang.blaze3d.platform.InputUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.OkZoomerConfigManager;
import io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

// The class that contains most of the logic behind the zoom itself
public class ZoomUtils {
    // The logger, used everywhere to print messages to the console
    public static final Logger LOGGER = LogManager.getFormatterLogger("Ok Zoomer");

    public static final ZoomInstance ZOOMER_ZOOM = new ZoomInstance(
        new Identifier("okzoomer:zoom"),
        4.0F,
        new SmoothTransitionMode(0.75f),
        new ZoomDivisorMouseModifier(),
        null
    );

    // The method used for changing the zoom divisor, used by zoom scrolling and the keybinds
    // TODO - Overhaul the scrolling system; I have an idea
    public static final void changeZoomDivisor(boolean increase) {
        //If the zoom is disabled, don't allow for zoom scrolling
        if (ZoomPackets.getDisableZoom() || ZoomPackets.getDisableZoomScrolling()) {
            return;
        }

        double zoomDivisor = ZOOMER_ZOOM.getZoomDivisor();
        double minimumZoomDivisor = OkZoomerConfigManager.configInstance.values().getMinimumZoomDivisor();
        double maximumZoomDivisor = OkZoomerConfigManager.configInstance.values().getMaximumZoomDivisor();

        double changedZoomDivisor;
        double lesserChangedZoomDivisor;

        if (ZoomPackets.getForceZoomDivisors()) {
            double packetMinimumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
            double packetMaximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();

            if (packetMinimumZoomDivisor < minimumZoomDivisor) {
                minimumZoomDivisor = packetMinimumZoomDivisor;
            }
            
            if (packetMaximumZoomDivisor > maximumZoomDivisor) {
                maximumZoomDivisor = packetMaximumZoomDivisor;
            }
        }

        if (increase) {
            changedZoomDivisor = zoomDivisor + OkZoomerConfigManager.configInstance.values().getScrollStep();
            lesserChangedZoomDivisor = zoomDivisor + OkZoomerConfigManager.configInstance.values().getLesserScrollStep();
        } else {
            changedZoomDivisor = zoomDivisor - OkZoomerConfigManager.configInstance.values().getScrollStep();
            lesserChangedZoomDivisor = zoomDivisor - OkZoomerConfigManager.configInstance.values().getLesserScrollStep();
        }

        if (lesserChangedZoomDivisor <= ZOOMER_ZOOM.getDefaultZoomDivisor()) {
            changedZoomDivisor = lesserChangedZoomDivisor;
        }

        if (changedZoomDivisor >= minimumZoomDivisor && changedZoomDivisor <= maximumZoomDivisor) {
            ZOOMER_ZOOM.setZoomDivisor(changedZoomDivisor);
        }
    }

    // The method used by both the "Reset Zoom" keybind and the "Reset Zoom With Mouse" tweak
    public static final void resetZoomDivisor() {
        if (ZoomPackets.getDisableZoom() || ZoomPackets.getDisableZoomScrolling()) {
            return;
        }

        ZOOMER_ZOOM.resetZoomDivisor();
    }

    // The method used for unbinding the "Save Toolbar Activator"
    public static final void unbindConflictingKey(MinecraftClient client, boolean userPrompted) {
        if (ZoomKeyBinds.ZOOM_KEY.isDefault()) {
            if (client.options.saveToolbarActivatorKey.isDefault()) {
                if (userPrompted) {
                    ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding...");
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.unbind_conflicting_key.success")));
                } else {
                    ZoomUtils.LOGGER.info("[Ok Zoomer] The \"Save Toolbar Activator\" keybind was occupying C! Unbinding... This process won't be repeated until specified in the config.");
                }
                client.options.saveToolbarActivatorKey.setBoundKey(InputUtil.UNKNOWN_KEY);
                client.options.write();
                KeyBind.updateBoundKeys();
            } else {
                ZoomUtils.LOGGER.info("[Ok Zoomer] No conflicts with the \"Save Toolbar Activator\" keybind were found!");
                if (userPrompted) {
                    client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT, new TranslatableText("toast.okzoomer.title"), new TranslatableText("toast.okzoomer.unbind_conflicting_key.no_conflict")));
                }
            }
        }
    }
}
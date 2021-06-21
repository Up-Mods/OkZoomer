package io.github.ennuil.okzoomer.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.NoMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.NoZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import io.github.ennuil.okzoomer.zoom.LinearTransitionMode;
import io.github.ennuil.okzoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.okzoomer.zoom.ZoomerZoomOverlay;
import net.fabricmc.loader.api.FabricLoader;

// TODO - Move to whatever Config API gets standarized for Quilt
// The class responsible for loading and saving the config.
public class OkZoomerConfig {
    public static boolean isConfigLoaded = false;
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("okzoomer.json5");
    private static final AnnotatedSettings ANNOTATED_SETTINGS = AnnotatedSettings.builder()
        .useNamingConvention(SettingNamingConvention.SNAKE_CASE)
        .build();
    private static final OkZoomerConfigPojo POJO = new OkZoomerConfigPojo();
    public static final ConfigTree TREE = ConfigTree.builder()
        .applyFromPojo(POJO, ANNOTATED_SETTINGS)
        .build();
    
    private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

    public enum ZoomPresets {
        DEFAULT,
        CLASSIC,
        PERSISTENT
    }

    public static void loadModConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                ANNOTATED_SETTINGS.applyToNode(TREE, POJO);
                FiberSerialization.deserialize(TREE, Files.newInputStream(CONFIG_PATH), serializer);
                configureZoomInstance();
                isConfigLoaded = true;
            } catch (IOException | FiberException e) {
                e.printStackTrace();
            }
        } else {
            saveModConfig();
            isConfigLoaded = true;
        }
    }

    public static void saveModConfig() {
        try {
            ANNOTATED_SETTINGS.applyToNode(TREE, POJO);
            FiberSerialization.serialize(TREE, Files.newOutputStream(CONFIG_PATH), serializer);
            configureZoomInstance();
        } catch (IOException | FiberException e) {
            e.printStackTrace();
        }
    }

    public static void configureZoomInstance() {
        // Sets zoom transition
        ZoomUtils.zoomerZoom.setTransitionMode(
            switch (OkZoomerConfigPojo.features.zoomTransition) {
                case SMOOTH -> new SmoothTransitionMode((float) OkZoomerConfigPojo.values.smoothMultiplier);
                case LINEAR -> new LinearTransitionMode(OkZoomerConfigPojo.values.minimumLinearStep, OkZoomerConfigPojo.values.maximumLinearStep);
                default -> new InstantTransitionMode();
            }
        );

        // Forces Classic Mode settings
        if (ZoomPackets.getForceClassicMode()) {
            ZoomUtils.zoomerZoom.setDefaultZoomDivisor(4.0D);
            ZoomUtils.zoomerZoom.setMouseModifier(new CinematicCameraMouseModifier());
            ZoomUtils.zoomerZoom.setZoomOverlay(new NoZoomOverlay());
            return;
        }

        // Sets zoom divisor
        ZoomUtils.zoomerZoom.setDefaultZoomDivisor(OkZoomerConfigPojo.values.zoomDivisor);

        // Sets mouse modifier
        configureZoomModifier();

        // Sets zoom overlay
        ZoomUtils.zoomerZoom.setZoomOverlay(
            OkZoomerConfigPojo.features.zoomOverlay
            ? new ZoomerZoomOverlay()
            : new NoZoomOverlay()
        );
    }

    public static void configureZoomModifier() {
        CinematicCameraOptions cinematicCamera = OkZoomerConfigPojo.features.cinematicCamera;
        boolean reduceSensitivity = OkZoomerConfigPojo.features.reduceSensitivity;
        if (cinematicCamera != CinematicCameraOptions.OFF) {
            MouseModifier cinematicModifier = switch (cinematicCamera) {
                case VANILLA -> new CinematicCameraMouseModifier();
                case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(OkZoomerConfigPojo.values.cinematicMultiplier);
                default -> null;
            };
            ZoomUtils.zoomerZoom.setMouseModifier(reduceSensitivity
                ? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
                : cinematicModifier
            );
        } else {
            ZoomUtils.zoomerZoom.setMouseModifier(reduceSensitivity
                ? new ZoomDivisorMouseModifier()
                : new NoMouseModifier()
            );
        }
    }

    public static void resetToPreset(ZoomPresets preset) {
        OkZoomerConfigPojo.features.cinematicCamera = switch (preset) {
            case CLASSIC -> CinematicCameraOptions.VANILLA;
            default -> CinematicCameraOptions.OFF;
        };
        OkZoomerConfigPojo.features.reduceSensitivity = switch (preset) {
            case CLASSIC -> false;
            default -> true;
        };
        OkZoomerConfigPojo.features.zoomTransition = switch (preset) {
            case CLASSIC -> ZoomTransitionOptions.OFF;
            default -> ZoomTransitionOptions.SMOOTH;
        };
        OkZoomerConfigPojo.features.zoomMode = switch (preset) {
            case PERSISTENT -> ZoomModes.PERSISTENT;
            default -> ZoomModes.HOLD;
        };
        OkZoomerConfigPojo.features.zoomScrolling = switch (preset) {
            case CLASSIC -> false;
            default -> true;
        };
        OkZoomerConfigPojo.features.extraKeybinds = switch (preset) {
            case CLASSIC -> false;
            default -> true;
        };
        OkZoomerConfigPojo.values.zoomDivisor = switch (preset) {
            case PERSISTENT -> 1.0;
            default -> 4.0;
        };
        OkZoomerConfigPojo.tweaks.resetZoomWithMouse = switch (preset) {
            case CLASSIC -> false;
            default -> true;
        };
        OkZoomerConfigPojo.features.zoomOverlay = false;
        OkZoomerConfigPojo.values.minimumZoomDivisor = 1.0;
        OkZoomerConfigPojo.values.maximumZoomDivisor = 50.0;
        OkZoomerConfigPojo.values.scrollStep = 1.0;
        OkZoomerConfigPojo.values.lesserScrollStep = 0.5;
        OkZoomerConfigPojo.values.cinematicMultiplier = 4.0;
        OkZoomerConfigPojo.values.smoothMultiplier = 0.75;
        OkZoomerConfigPojo.values.minimumLinearStep = 0.125;
        OkZoomerConfigPojo.values.maximumLinearStep = 0.25;
        OkZoomerConfigPojo.tweaks.printOwoOnStart = true;
    }
}
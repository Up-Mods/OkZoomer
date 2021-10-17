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
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomOverlays;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import io.github.ennuil.okzoomer.zoom.LinearTransitionMode;
import io.github.ennuil.okzoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.okzoomer.zoom.ZoomerZoomOverlay;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

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
        PERSISTENT,
        SPYGLASS
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
        Identifier overlayTextureId = new Identifier(
            OkZoomerConfigPojo.tweaks.useSpyglassTexture
            ? "textures/misc/spyglass_scope.png"
            : "okzoomer:textures/misc/zoom_overlay.png");

        ZoomUtils.zoomerZoom.setZoomOverlay(
            switch (OkZoomerConfigPojo.features.zoomOverlay) {
                case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
                case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
                default -> new NoZoomOverlay();
            }
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
        OkZoomerConfigPojo.features.cinematicCamera = preset == ZoomPresets.CLASSIC ? CinematicCameraOptions.VANILLA : CinematicCameraOptions.OFF;
        OkZoomerConfigPojo.features.reduceSensitivity = preset == ZoomPresets.CLASSIC ? false : true;
        OkZoomerConfigPojo.features.zoomTransition = preset == ZoomPresets.CLASSIC ? ZoomTransitionOptions.OFF : ZoomTransitionOptions.SMOOTH;
        OkZoomerConfigPojo.features.zoomMode = preset == ZoomPresets.PERSISTENT ? ZoomModes.PERSISTENT : ZoomModes.HOLD;
        OkZoomerConfigPojo.features.zoomScrolling = switch (preset) {
            case CLASSIC -> false;
            case SPYGLASS -> false;
            default -> true;
        };
        OkZoomerConfigPojo.features.extraKeybinds = preset == ZoomPresets.CLASSIC ? false : true;
        OkZoomerConfigPojo.features.zoomOverlay = preset == ZoomPresets.SPYGLASS ? ZoomOverlays.SPYGLASS : ZoomOverlays.OFF;
        OkZoomerConfigPojo.values.zoomDivisor  = switch (preset) {
            case PERSISTENT -> 1.0D;
            case SPYGLASS -> 10.0D;
            default -> 4.0D;
        };
        OkZoomerConfigPojo.values.smoothMultiplier = preset == ZoomPresets.SPYGLASS ? 0.5D : 0.75D;
        OkZoomerConfigPojo.tweaks.useSpyglassTexture = preset == ZoomPresets.SPYGLASS ? true : false;
        OkZoomerConfigPojo.tweaks.useSpyglassSounds = preset == ZoomPresets.SPYGLASS ? true : false;
        OkZoomerConfigPojo.tweaks.resetZoomWithMouse = preset == ZoomPresets.CLASSIC ? false : true;

        OkZoomerConfigPojo.values.minimumZoomDivisor = 1.0D;
        OkZoomerConfigPojo.values.maximumZoomDivisor = 50.0D;
        OkZoomerConfigPojo.values.scrollStep = 1.0D;
        OkZoomerConfigPojo.values.lesserScrollStep = 0.5D;
        OkZoomerConfigPojo.values.cinematicMultiplier = 4.0D;
        OkZoomerConfigPojo.values.minimumLinearStep = 0.125D;
        OkZoomerConfigPojo.values.maximumLinearStep = 0.25D;
        OkZoomerConfigPojo.tweaks.unbindConflictingKey = false;
        OkZoomerConfigPojo.tweaks.printOwoOnStart = true;
    }
}
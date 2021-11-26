package io.github.ennuil.okzoomer.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.mojang.serialization.JsonOps;

import org.quiltmc.json5.JsonReader;
import org.quiltmc.json5.JsonWriter;
import org.quiltmc.json5.exception.MalformedSyntaxException;

import io.github.ennuil.libzoomer.api.MouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.okzoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.okzoomer.config.codec.FeaturesConfig;
import io.github.ennuil.okzoomer.config.codec.OkZoomerConfig;
import io.github.ennuil.okzoomer.config.codec.TweaksConfig;
import io.github.ennuil.okzoomer.config.codec.ValuesConfig;
import io.github.ennuil.okzoomer.config.json5.Json5Helper;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import io.github.ennuil.okzoomer.zoom.LinearTransitionMode;
import io.github.ennuil.okzoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.okzoomer.zoom.ZoomerZoomOverlay;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

// The class responsible for loading and saving the config.
public class OkZoomerConfigManager {
    public static Optional<Boolean> isConfigLoaded = Optional.empty();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("okzoomer.json5");
    public static OkZoomerConfig INSTANCE = OkZoomerConfig.getDefaultSettings();

    public enum ZoomPresets {
        DEFAULT,
        CLASSIC,
        PERSISTENT,
        SPYGLASS
    }

    public static void loadModConfig() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                JsonReader reader = JsonReader.json5(CONFIG_PATH);
                var result = OkZoomerConfig.CODEC.parse(JsonOps.INSTANCE, Json5Helper.parseJson5Reader(reader)).result();
                reader.close();
                if (result.isPresent()) {
                    INSTANCE = result.get();
                    configureZoomInstance();
                    isConfigLoaded = Optional.of(true);
                } else {
                    ZoomUtils.LOGGER.error("Failed to load the settings!");
                    isConfigLoaded = Optional.of(false);
                }
            } catch (MalformedSyntaxException | IOException e) {
                ZoomUtils.LOGGER.error("Failed to load the settings!\n" + e.toString());
            }
        } else {
            saveModConfig();
            isConfigLoaded = Optional.of(true);
        }
    }

    public static void saveModConfig() {
        var result = OkZoomerConfig.CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE).result();
        if (result.isPresent()) {
            try {
                JsonWriter writer = JsonWriter.json5(CONFIG_PATH);
                writer.beginObject()
                // Features
                .name("features").beginObject()
                    // Cinematic Camera
                    .comment("""
                        Defines the cinematic camera while zooming.
                        "OFF" disables the cinematic camera.
                        "VANILLA" uses Vanilla's cinematic camera.
                        "MULTIPLIED" is a multiplied variant of "VANILLA".
                        """).name("cinematic_camera").value(INSTANCE.features().cinematicCamera().asString())
                    
                    // Reduce Sensitivity
                    .comment("Reduces the mouse sensitivity when zooming.")
                    .name("reduce_sensitivity").value(INSTANCE.features().reduceSensitivity())

                    // Zoom Transition
                    .comment("""
                        Adds transitions between zooms.
                        "OFF" disables transitions.
                        "SMOOTH" replicates Vanilla's dynamic FOV.
                        "LINEAR" removes the smoothiness.
                        """).name("zoom_transition").value(INSTANCE.features().zoomTransition().asString())

                    // Zoom Mode
                    .comment("""
                        The behavior of the zoom key.
                        "HOLD" needs the zoom key to be hold.
                        "TOGGLE" has the zoom key toggle the zoom.
                        "PERSISTENT" makes the zoom permanent.
                        """).name("zoom_mode").value(INSTANCE.features().zoomMode().asString())

                    // Zoom Scrolling
                    .comment("Allows to increase or decrease zoom by scrolling.")
                    .name("zoom_scrolling").value(INSTANCE.features().zoomScrolling())

                    // Extra Keybinds
                    .comment("Adds zoom manipulation keys along with the zoom key.")
                    .name("extra_keybinds").value(INSTANCE.features().extraKeybinds())
                    
                    // Zoom Overlay
                    .comment("""
                        Adds an overlay in the screen during zoom.
                        "VIGNETTE" uses a vignette as the overlay.
                        "SPYGLASS" uses the spyglass overlay with the vignette texture.
                        The vignette texture can be found at: assets/okzoomer/textures/misc/zoom_overlay.png
                        """).name("zoom_overlay").value(INSTANCE.features().zoomOverlay().asString())

                .endObject()

                // Values
                .name("values").beginObject()
                    // Zoom Divisor
                    .comment("The divisor applied to the FOV when zooming.")
                    .name("zoom_divisor").value(INSTANCE.values().zoomDivisor())

                    // Minimum Zoom Divisor
                    .comment("The minimum value that you can scroll down.")
                    .name("minimum_zoom_divisor").value(INSTANCE.values().minimumZoomDivisor())

                    // Maximum Zoom Divisor
                    .comment("The maximum value that you can scroll down.")
                    .name("maximum_zoom_divisor").value(INSTANCE.values().maximumZoomDivisor())

                    // Scroll Step
                    .comment("""
                        The number which is decremented or incremented by zoom scrolling.
                        Used when the zoom divisor is above the starting point.
                        """).name("scroll_step").value(INSTANCE.values().scrollStep())

                    // Lesser Scroll Step
                    .comment("""
                        The number which is decremented or incremented by zoom scrolling.
                        Used when the zoom divisor is below the starting point.
                        """).name("lesser_scroll_step").value(INSTANCE.values().lesserScrollStep())

                    // Smooth Multiplier
                    .comment("The multiplier used for smooth transitions.")
                    .name("smooth_multiplier").value(INSTANCE.values().smoothMultiplier())

                    // Cinematic Multiplier
                    .comment("The multiplier used for the multiplied cinematic camera.")
                    .name("cinematic_multiplier").value(INSTANCE.values().cinematicMultiplier())

                    // Minimum Linear Step
                    .comment("The minimum value which the linear transition step can reach.")
                    .name("minimum_linear_step").value(INSTANCE.values().minimumLinearStep())

                    // Maximum Linear Step
                    .comment("The maximum value which the linear transition step can reach.")
                    .name("maximum_linear_step").value(INSTANCE.values().maximumLinearStep())
                .endObject()

                // Tweaks
                .name("tweaks").beginObject()
                    // Reset Zoom with Mouse
                    .comment("Allows for resetting the zoom with the middle mouse button.")
                    .name("reset_zoom_with_mouse").value(INSTANCE.tweaks().resetZoomWithMouse())

                    // Unbind Conflicting Key
                    .comment("If pressed, the \"Save Toolbar Activator\" keybind will be unbound if there's a conflict with the zoom key.")
                    .name("unbind_conflicting_key").value(INSTANCE.tweaks().unbindConflictingKey())

                    // Use Spyglass Texture
                    .comment("If enabled, the spyglass overlay texture is used instead of Ok Zoomer's overlay texture.")
                    .name("use_spyglass_texture").value(INSTANCE.tweaks().useSpyglassTexture())

                    // Use Spyglass Sounds
                    .comment("If enabled, the zoom will use spyglass sounds on zooming in and out.")
                    .name("use_spyglass_sounds").value(INSTANCE.tweaks().useSpyglassSounds())

                    // Show Restriction Toasts
                    .comment("Shows toasts when the server imposes a restriction.")
                    .name("show_restriction_toasts").value(INSTANCE.tweaks().showRestrictionToasts())

                    // Print owo on Start
                    // TODO - Revert on 5.0.0
                    .comment("Prints a random owo in the console when the game starts. Enabled by default until full release.")
                    .name("print_owo_on_start").value(INSTANCE.tweaks().printOwoOnStart())
                .endObject()

                .endObject();
                writer.close();
                configureZoomInstance();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void configureZoomInstance() {
        // Sets zoom transition
        ZoomUtils.zoomerZoom.setTransitionMode(
            switch (INSTANCE.features().zoomTransition()) {
                case SMOOTH -> new SmoothTransitionMode((float) INSTANCE.values().smoothMultiplier());
                case LINEAR -> new LinearTransitionMode(INSTANCE.values().minimumLinearStep(), INSTANCE.values().maximumLinearStep());
                default -> new InstantTransitionMode();
            }
        );

        // Forces Classic Mode settings
        if (ZoomPackets.getForceClassicMode()) {
            ZoomUtils.zoomerZoom.setDefaultZoomDivisor(4.0D);
            ZoomUtils.zoomerZoom.setMouseModifier(new CinematicCameraMouseModifier());
            ZoomUtils.zoomerZoom.setZoomOverlay(null);
            return;
        }

        // Sets zoom divisor
        ZoomUtils.zoomerZoom.setDefaultZoomDivisor(INSTANCE.values().zoomDivisor());

        // Sets mouse modifier
        configureZoomModifier();

        // Sets zoom overlay
        Identifier overlayTextureId = new Identifier(
            INSTANCE.tweaks().useSpyglassTexture()
            ? "textures/misc/spyglass_scope.png"
            : "okzoomer:textures/misc/zoom_overlay.png");

        ZoomUtils.zoomerZoom.setZoomOverlay(
            switch (INSTANCE.features().zoomOverlay()) {
                case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
                case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
                default -> null;
            }
        );
    }

    public static void configureZoomModifier() {
        CinematicCameraOptions cinematicCamera = INSTANCE.features().cinematicCamera();
        boolean reduceSensitivity = INSTANCE.features().reduceSensitivity();
        if (cinematicCamera != CinematicCameraOptions.OFF) {
            MouseModifier cinematicModifier = switch (cinematicCamera) {
                case VANILLA -> new CinematicCameraMouseModifier();
                case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(INSTANCE.values().cinematicMultiplier());
                default -> null;
            };
            ZoomUtils.zoomerZoom.setMouseModifier(reduceSensitivity
                ? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
                : cinematicModifier
            );
        } else {
            ZoomUtils.zoomerZoom.setMouseModifier(reduceSensitivity
                ? new ZoomDivisorMouseModifier()
                : null
            );
        }
    }

    public static void resetToPreset(ZoomPresets preset) {
        OkZoomerConfigManager.INSTANCE = new OkZoomerConfig(
            new FeaturesConfig(
                preset == ZoomPresets.CLASSIC ? CinematicCameraOptions.VANILLA : CinematicCameraOptions.OFF,
                preset == ZoomPresets.CLASSIC ? false : true,
                preset == ZoomPresets.CLASSIC ? ZoomTransitionOptions.OFF : ZoomTransitionOptions.SMOOTH,
                preset == ZoomPresets.PERSISTENT ? ZoomModes.PERSISTENT : ZoomModes.HOLD,
                switch (preset) {
                    case CLASSIC -> false;
                    case SPYGLASS -> false;
                    default -> true;
                },
                preset == ZoomPresets.CLASSIC ? false : true,
                preset == ZoomPresets.SPYGLASS ? ZoomOverlays.SPYGLASS : ZoomOverlays.OFF
            ),
            new ValuesConfig(
                switch (preset) {
                    case PERSISTENT -> 1.0D;
                    case SPYGLASS -> 10.0D;
                    default -> 4.0D;
                },
                1.0D,
                50.0D,
                1.0D,
                0.5D,
                preset == ZoomPresets.SPYGLASS ? 0.5D : 0.75D,
                4.0D,
                0.125D,
                0.25D
            ),
            new TweaksConfig(
                preset == ZoomPresets.CLASSIC ? false : true,
                false,
                preset == ZoomPresets.SPYGLASS ? true : false,
                preset == ZoomPresets.SPYGLASS ? true : false,
                true,
                preset == ZoomPresets.CLASSIC ? false : true
            )
        );
    }
}
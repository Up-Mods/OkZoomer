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
import io.github.ennuil.okzoomer.config.ConfigEnums.SpyglassDependency;
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

// The class responsible for loading and saving the config
public class OkZoomerConfigManager {
    public static Optional<Boolean> isConfigLoaded = Optional.empty();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("okzoomer.json5");
    public static OkZoomerConfig configInstance = OkZoomerConfig.getDefaultSettings();

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
                    configInstance = result.get();
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
        var result = OkZoomerConfig.CODEC.encodeStart(JsonOps.INSTANCE, configInstance).result();
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
                        """).name("cinematic_camera").value(configInstance.features().getCinematicCamera().asString())
                    
                    // Reduce Sensitivity
                    .comment("Reduces the mouse sensitivity when zooming.")
                    .name("reduce_sensitivity").value(configInstance.features().getReduceSensitivity())

                    // Zoom Transition
                    .comment("""
                        Adds transitions between zooms.
                        "OFF" disables transitions.
                        "SMOOTH" replicates Vanilla's dynamic FOV.
                        "LINEAR" removes the smoothiness.
                        """).name("zoom_transition").value(configInstance.features().getZoomTransition().asString())

                    // Zoom Mode
                    .comment("""
                        The behavior of the zoom key.
                        "HOLD" needs the zoom key to be hold.
                        "TOGGLE" has the zoom key toggle the zoom.
                        "PERSISTENT" makes the zoom permanent.
                        """).name("zoom_mode").value(configInstance.features().getZoomMode().asString())

                    // Zoom Scrolling
                    .comment("Allows to increase or decrease zoom by scrolling.")
                    .name("zoom_scrolling").value(configInstance.features().getZoomScrolling())

                    // Extra Keybinds
                    .comment("Adds zoom manipulation keys along with the zoom key.")
                    .name("extra_key_binds").value(configInstance.features().getExtraKeyBinds())
                    
                    // Zoom Overlay
                    .comment("""
                        Adds an overlay in the screen during zoom.
                        "VIGNETTE" uses a vignette as the overlay.
                        "SPYGLASS" uses the spyglass overlay with the vignette texture.
                        The vignette texture can be found at: assets/okzoomer/textures/misc/zoom_overlay.png
                        """).name("zoom_overlay").value(configInstance.features().getZoomOverlay().asString())
                    
                    // Spyglass Dependency
                    .comment("""
                        Determines how the zoom will depend on the spyglass.
                        "REQUIRE_ITEM" will make zooming require a spyglass.
                        "REPLACE_ZOOM" will replace spyglass's zoom with Ok Zoomer's zoom.
                        "BOTH" will apply both options at the same time.
                        The "REQUIRE_ITEM" option is configurable through the okzoomer:zoom_dependencies item tag.
                        """).name("spyglass_dependency").value(configInstance.features().getSpyglassDependency().asString())

                .endObject()

                // Values
                .name("values").beginObject()
                    // Zoom Divisor
                    .comment("The divisor applied to the FOV when zooming.")
                    .name("zoom_divisor").value(configInstance.values().getZoomDivisor())

                    // Minimum Zoom Divisor
                    .comment("The minimum value that you can scroll down.")
                    .name("minimum_zoom_divisor").value(configInstance.values().getMinimumZoomDivisor())

                    // Maximum Zoom Divisor
                    .comment("The maximum value that you can scroll down.")
                    .name("maximum_zoom_divisor").value(configInstance.values().getMaximumZoomDivisor())

                    // Scroll Step
                    .comment("""
                        The number of steps between the zoom divisor and the maximum zoom divisor.
                        Used by zoom scrolling.
                        """).name("upper_scroll_steps").value(configInstance.values().getUpperScrollSteps())

                    // Lesser Scroll Step
                    .comment("""
                        The number of steps between the zoom divisor and the minimum zoom divisor.
                        Used by zoom scrolling.
                        """).name("lower_scroll_steps").value(configInstance.values().getLowerScrollSteps())

                    // Smooth Multiplier
                    .comment("The multiplier used for smooth transitions.")
                    .name("smooth_multiplier").value(configInstance.values().getSmoothMultiplier())

                    // Cinematic Multiplier
                    .comment("The multiplier used for the multiplied cinematic camera.")
                    .name("cinematic_multiplier").value(configInstance.values().getCinematicMultiplier())

                    // Minimum Linear Step
                    .comment("The minimum value which the linear transition step can reach.")
                    .name("minimum_linear_step").value(configInstance.values().getMinimumLinearStep())

                    // Maximum Linear Step
                    .comment("The maximum value which the linear transition step can reach.")
                    .name("maximum_linear_step").value(configInstance.values().getMaximumLinearStep())
                .endObject()

                // Tweaks
                .name("tweaks").beginObject()
                    // Reset Zoom with Mouse
                    .comment("Allows for resetting the zoom with the middle mouse button.")
                    .name("reset_zoom_with_mouse").value(configInstance.tweaks().getResetZoomWithMouse())

                    // Unbind Conflicting Key
                    .comment("If pressed, the \"Save Toolbar Activator\" keybind will be unbound if there's a conflict with the zoom key.")
                    .name("unbind_conflicting_key").value(configInstance.tweaks().getUnbindConflictingKey())

                    // Use Spyglass Texture
                    .comment("If enabled, the spyglass overlay texture is used instead of Ok Zoomer's overlay texture.")
                    .name("use_spyglass_texture").value(configInstance.tweaks().getUseSpyglassTexture())

                    // Use Spyglass Sounds
                    .comment("If enabled, the zoom will use spyglass sounds on zooming in and out.")
                    .name("use_spyglass_sounds").value(configInstance.tweaks().getUseSpyglassSounds())

                    // Show Restriction Toasts
                    .comment("Shows toasts when the server imposes a restriction.")
                    .name("show_restriction_toasts").value(configInstance.tweaks().getShowRestrictionToasts())

                    // Print owo on Start
                    // TODO - Revert on 5.0.0
                    .comment("Prints a random owo in the console when the game starts. Enabled by default until full release.")
                    .name("print_owo_on_start").value(configInstance.tweaks().getPrintOwoOnStart())
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
        ZoomUtils.ZOOMER_ZOOM.setTransitionMode(
            switch (configInstance.features().getZoomTransition()) {
                case SMOOTH -> new SmoothTransitionMode((float) configInstance.values().getSmoothMultiplier());
                case LINEAR -> new LinearTransitionMode(configInstance.values().getMinimumLinearStep(), configInstance.values().getMaximumLinearStep());
                default -> new InstantTransitionMode();
            }
        );

        // Forces Classic Mode settings
        if (ZoomPackets.getForceClassicMode()) {
            ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(4.0D);
            ZoomUtils.ZOOMER_ZOOM.setMouseModifier(new CinematicCameraMouseModifier());
            ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(null);
            return;
        }

        // Sets zoom divisor
        ZoomUtils.ZOOMER_ZOOM.setDefaultZoomDivisor(configInstance.values().getZoomDivisor());

        // Sets mouse modifier
        configureZoomModifier();

        // Sets zoom overlay
        Identifier overlayTextureId = new Identifier(
            configInstance.tweaks().getUseSpyglassTexture()
            ? "textures/misc/spyglass_scope.png"
            : "okzoomer:textures/misc/zoom_overlay.png");
        
        // Enforce spyglass overlay if necessary
        ZoomOverlays overlay = ZoomPackets.getSpyglassOverlay() ? ZoomOverlays.SPYGLASS : configInstance.features().getZoomOverlay(); 

        ZoomUtils.ZOOMER_ZOOM.setZoomOverlay(
            switch (overlay) {
                case VIGNETTE -> new ZoomerZoomOverlay(overlayTextureId);
                case SPYGLASS -> new SpyglassZoomOverlay(overlayTextureId);
                default -> null;
            }
        );
    }

    public static void configureZoomModifier() {
        CinematicCameraOptions cinematicCamera = configInstance.features().getCinematicCamera();
        boolean reduceSensitivity = configInstance.features().getReduceSensitivity();
        if (cinematicCamera != CinematicCameraOptions.OFF) {
            MouseModifier cinematicModifier = switch (cinematicCamera) {
                case VANILLA -> new CinematicCameraMouseModifier();
                case MULTIPLIED -> new MultipliedCinematicCameraMouseModifier(configInstance.values().getCinematicMultiplier());
                default -> null;
            };
            ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity
                ? new ContainingMouseModifier(cinematicModifier, new ZoomDivisorMouseModifier())
                : cinematicModifier
            );
        } else {
            ZoomUtils.ZOOMER_ZOOM.setMouseModifier(reduceSensitivity
                ? new ZoomDivisorMouseModifier()
                : null
            );
        }
    }

    public static void resetToPreset(ZoomPresets preset) {
        OkZoomerConfigManager.configInstance = new OkZoomerConfig(
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
                preset == ZoomPresets.SPYGLASS ? ZoomOverlays.SPYGLASS : ZoomOverlays.OFF,
                preset == ZoomPresets.SPYGLASS ? SpyglassDependency.BOTH : SpyglassDependency.OFF
            ),
            new ValuesConfig(
                switch (preset) {
                    case PERSISTENT -> 1.0D;
                    case SPYGLASS -> 10.0D;
                    default -> 4.0D;
                },
                1.0D,
                50.0D,
                preset == ZoomPresets.SPYGLASS ? 16 : 20,
                preset == ZoomPresets.SPYGLASS ? 8 : 4,
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
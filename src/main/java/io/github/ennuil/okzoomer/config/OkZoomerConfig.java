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
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.libzoomer.api.modifiers.CinematicCameraMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ContainingMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.NoMouseModifier;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.NoZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.InstantTransitionMode;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.ennuil.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.ennuil.okzoomer.packets.ZoomPackets;
import io.github.ennuil.okzoomer.utils.ZoomUtils;
import io.github.ennuil.okzoomer.zoom.LinearTransitionMode;
import io.github.ennuil.okzoomer.zoom.MultipliedCinematicCameraMouseModifier;
import io.github.ennuil.okzoomer.zoom.ZoomerZoomOverlay;
import net.fabricmc.loader.api.FabricLoader;

// TODO - Move to whatever Config API gets standarized for Quilt
//The class responsible for loading and saving the config.
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
				? new ContainingMouseModifier(new MouseModifier[]{cinematicModifier, new ZoomDivisorMouseModifier()})
				: cinematicModifier
			);
		} else {
			ZoomUtils.zoomerZoom.setMouseModifier(reduceSensitivity
				? new ZoomDivisorMouseModifier()
				: new NoMouseModifier()
			);
		}
	}
}
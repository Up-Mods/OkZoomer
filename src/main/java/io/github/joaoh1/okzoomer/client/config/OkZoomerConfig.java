package io.github.joaoh1.okzoomer.client.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import net.fabricmc.loader.api.FabricLoader;

//The class responsible for loading and saving the config.
//TODO - Remove backward-compatibility on the next major MC version.
public class OkZoomerConfig {
	public static boolean isConfigLoaded = false;
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("okzoomer.json5");
	public static final Path LEGACY_CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("okzoomer-legacy.json5");
	private static final AnnotatedSettings ANNOTATED_SETTINGS = AnnotatedSettings.builder()
		.useNamingConvention(SettingNamingConvention.SNAKE_CASE)
		.build();
	private static final OkZoomerConfigPojo POJO = new OkZoomerConfigPojo();
	public static final ConfigTree TREE = ConfigTree.builder()
		.applyFromPojo(POJO, ANNOTATED_SETTINGS)
		.build();
	private static final AnnotatedSettings LEGACY_ANNOTATED_SETTINGS = AnnotatedSettings.builder()
		.useNamingConvention(SettingNamingConvention.NONE)
		.build();
	private static final OkZoomerLegacyConfigPojo LEGACY_POJO = new OkZoomerLegacyConfigPojo();
	public static final ConfigTree LEGACY_TREE = ConfigTree.builder()
		.applyFromPojo(LEGACY_POJO, LEGACY_ANNOTATED_SETTINGS)
		.build();
	
	private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

	public static void loadModConfig() {
		if (Files.exists(CONFIG_PATH)) {
			try {
				//If the legacy config file is detected, translate it to the new format.
				Files.readAllLines(CONFIG_PATH).forEach(string -> {
					if (string.contains("\"hideHands\":")) {
						try {
							ZoomUtils.modLogger.info("[Ok Zoomer] A config file from the 4.0.0 alphas was found! It will be converted to the new format then used.");
							LEGACY_ANNOTATED_SETTINGS.applyToNode(LEGACY_TREE, LEGACY_POJO);
							FiberSerialization.deserialize(TREE, Files.newInputStream(CONFIG_PATH), serializer);
							Files.copy(CONFIG_PATH, LEGACY_CONFIG_PATH);
							OkZoomerConfigPojo.values.zoomDivisor = OkZoomerLegacyConfigPojo.zoomDivisor;
							OkZoomerConfigPojo.values.minimumZoomDivisor = OkZoomerLegacyConfigPojo.minimumZoomDivisor;
							OkZoomerConfigPojo.values.maximumZoomDivisor = OkZoomerLegacyConfigPojo.maximumZoomDivisor;
							OkZoomerConfigPojo.features.cinematicCamera = OkZoomerLegacyConfigPojo.smoothCamera ? CinematicCameraOptions.VANILLA : CinematicCameraOptions.OFF;
							OkZoomerConfigPojo.features.reduceSensitivity = OkZoomerLegacyConfigPojo.reduceSensitivity;
							OkZoomerConfigPojo.features.zoomTransition = OkZoomerLegacyConfigPojo.smoothTransition ? ZoomTransitionOptions.SMOOTH : ZoomTransitionOptions.OFF;
							OkZoomerConfigPojo.features.zoomMode = OkZoomerLegacyConfigPojo.zoomToggle ? ZoomModes.TOGGLE : ZoomModes.HOLD;
							OkZoomerConfigPojo.features.zoomScrolling = OkZoomerLegacyConfigPojo.zoomScrolling;
							OkZoomerConfigPojo.features.extraKeybinds = OkZoomerLegacyConfigPojo.zoomScrolling;
							saveModConfig();
						} catch (IOException | FiberException e) {
							e.printStackTrace();
						}
						return;
					}
				});
				ANNOTATED_SETTINGS.applyToNode(TREE, POJO);
				FiberSerialization.deserialize(TREE, Files.newInputStream(CONFIG_PATH), serializer);
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
		} catch (IOException | FiberException e) {
			e.printStackTrace();
		}
	}
}
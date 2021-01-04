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
import io.github.joaoh1.libzoomer.api.ZoomOverlay;
import io.github.joaoh1.libzoomer.api.overlays.NoZoomOverlay;
import io.github.joaoh1.libzoomer.api.transitions.InstantTransitionMode;
import io.github.joaoh1.libzoomer.api.transitions.SmoothTransitionMode;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.joaoh1.okzoomer.client.utils.ZoomUtils;
import io.github.joaoh1.okzoomer.client.zoom.LinearTransitionMode;
import io.github.joaoh1.okzoomer.client.zoom.ZoomerZoomOverlay;
import net.fabricmc.loader.api.FabricLoader;

//The class responsible for loading and saving the config.
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
		ZoomUtils.zoomerZoom.setDefaultZoomDivisor(OkZoomerConfigPojo.values.zoomDivisor);
		System.out.println(OkZoomerConfigPojo.features.zoomTransition);
		if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.SMOOTH)) {
			ZoomUtils.zoomerZoom.setTransitionMode(new SmoothTransitionMode((float) OkZoomerConfigPojo.values.smoothMultiplier));
		} else if (OkZoomerConfigPojo.features.zoomTransition.equals(ZoomTransitionOptions.LINEAR)) {
			ZoomUtils.zoomerZoom.setTransitionMode(new LinearTransitionMode(OkZoomerConfigPojo.values.minimumLinearStep, OkZoomerConfigPojo.values.maximumLinearStep));
		} else {
			ZoomUtils.zoomerZoom.setTransitionMode(new InstantTransitionMode());
		}
		ZoomOverlay overlay = ZoomUtils.zoomerZoom.getZoomOverlay();
		if (OkZoomerConfigPojo.features.zoomOverlay && overlay instanceof NoZoomOverlay) {
			ZoomUtils.zoomerZoom.setZoomOverlay(new ZoomerZoomOverlay());
		} else if (!OkZoomerConfigPojo.features.zoomOverlay  && overlay instanceof ZoomerZoomOverlay) {
			ZoomUtils.zoomerZoom.setZoomOverlay(new NoZoomOverlay());
		}
	}
}
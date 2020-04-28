package io.github.joaoh1.okzoomer.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import me.zeroeightsix.fiber.api.exception.FiberException;
import me.zeroeightsix.fiber.api.serialization.JanksonSerializer;
import me.zeroeightsix.fiber.api.tree.ConfigBranch;
import me.zeroeightsix.fiber.api.tree.ConfigTree;
import me.zeroeightsix.fiber.api.tree.PropertyMirror;

public class OkZoomerConfig {
	// TODO - Organize the config in categories
	public static final PropertyMirror<Boolean> cinematicCamera = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> useAlternativeSmoothing = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> reduceSensitivity = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> hideHands = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> smoothTransition = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> zoomToggle = new PropertyMirror<>();
	public static final PropertyMirror<Double> zoomDivisor = new PropertyMirror<>();
	public static final PropertyMirror<Boolean> zoomScrolling = new PropertyMirror<>();
	public static final PropertyMirror<Double> minimumZoomDivisor = new PropertyMirror<>();
	public static final PropertyMirror<Double> maximumZoomDivisor = new PropertyMirror<>();

	public static final ConfigBranch node = ConfigTree.builder()
		.beginValue("cinematic_camera", Boolean.class, true)
			.withComment("Enables the cinematic camera while zooming.")
		.finishValue(cinematicCamera::mirror)
		.beginValue("use_alternative_smoothing", Boolean.class, true)
			.withComment("Makes the zoom's cinematic camera linger less.")
		.finishValue(useAlternativeSmoothing::mirror)
		.beginValue("reduce_sensitivity", Boolean.class, false)
			.withComment("Reduces the mouse sensitivity when zooming.")
		.finishValue(reduceSensitivity::mirror)
		.beginValue("smooth_transition", Boolean.class, false)
			.withComment("Enables smooth transitions when zooming in and out.")
		.finishValue(smoothTransition::mirror)
		.beginValue("zoom_toggle", Boolean.class, false)
			.withComment("Enables the ability to toggle zooming.")
		.finishValue(zoomToggle::mirror)
		.beginValue("zoom_divisor", Double.class, 4.0D)
			.withComment("The divisor applied to the FOV when zooming.")
			.beginConstraints()
				.atLeast(Double.MIN_VALUE)
			.finishConstraints()
		.finishValue(zoomDivisor::mirror)
		.beginValue("zoom_scrolling", Boolean.class, false)
			.withComment("Allows to increase or decrease zoom by scrolling.")
		.finishValue(zoomScrolling::mirror)
		.beginValue("minimum_zoom_divisor", Double.class, 1.0D)
			.withComment("The minimum value that you can scroll down.")
		.finishValue(minimumZoomDivisor::mirror)
		.beginValue("maximum_zoom_divisor", Double.class, 50.0D)
			.withComment("The maximum value that you can scroll up.")
		.finishValue(maximumZoomDivisor::mirror)
		.build();

	private static JanksonSerializer serializer = new JanksonSerializer();

	public static void loadJanksonConfig() {
		if (Files.exists(Paths.get("./config/okzoomer.json5"))) {
			try {
				serializer.deserialize(node, Files.newInputStream(Paths.get("./config/okzoomer.json5")));
			} catch (IOException | FiberException e) {
				e.printStackTrace();
			}
		} else {
			saveJanksonConfig();
		}
	}

	public static void saveJanksonConfig() {
		try {
			serializer.serialize(node, Files.newOutputStream(Paths.get("./config/okzoomer.json5")));
		} catch (IOException | FiberException e) {
			e.printStackTrace();
		}
	}
}
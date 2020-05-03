package io.github.joaoh1.okzoomer.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public class OkZoomerConfig {
	//TODO - Organize the config in categories
	public static final PropertyMirror<String> cinematicCamera = PropertyMirror.create(ConfigTypes.STRING);
	public static final PropertyMirror<Boolean> reduceSensitivity = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Boolean> hideHands = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Boolean> smoothTransition = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Boolean> zoomToggle = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Double> zoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE);
	public static final PropertyMirror<Boolean> zoomScrolling = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Double> minimumZoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE);
	public static final PropertyMirror<Double> maximumZoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE);

	public static final ConfigBranch node = ConfigTree.builder()
		.beginValue("cinematic_camera", ConfigTypes.STRING, "off")
			.withComment("Enables the cinematic camera while zooming.\n\"off\" disables it.\n\"vanilla\" mimics Vanilla's Cinematic Camera.\n\"4x\" is a less-lingering variant of \"vanilla\".")
			/*TODO - Readd the limits after Fiber is updated
			.beginConstraints()
				.regex("^off$|^vanilla$|^4x$")
			.finishConstraints()
			*/
		.finishValue(cinematicCamera::mirror)
		.beginValue("reduce_sensitivity", ConfigTypes.BOOLEAN, false)
			.withComment("Reduces the mouse sensitivity when zooming.")
		.finishValue(reduceSensitivity::mirror)
		.beginValue("smooth_transition", ConfigTypes.BOOLEAN, false)
			.withComment("Enables smooth transitions when zooming in and out.")
		.finishValue(smoothTransition::mirror)
		.beginValue("zoom_toggle", ConfigTypes.BOOLEAN, false)
			.withComment("Enables the ability to toggle zooming.")
		.finishValue(zoomToggle::mirror)
		.beginValue("zoom_divisor", ConfigTypes.DOUBLE, 4.0D)
			.withComment("The divisor applied to the FOV when zooming.")
			/*TODO - Readd the limits after Fiber is updated
			.beginConstraints()
				.atLeast(Double.MIN_VALUE)
			.finishConstraints()
			*/
		.finishValue(zoomDivisor::mirror)
		.beginValue("zoom_scrolling", ConfigTypes.BOOLEAN, false)
			.withComment("Allows to increase or decrease zoom by scrolling.")
		.finishValue(zoomScrolling::mirror)
		.beginValue("minimum_zoom_divisor", ConfigTypes.DOUBLE, 1.0D)
			.withComment("The minimum value that you can scroll down.")
		.finishValue(minimumZoomDivisor::mirror)
		.beginValue("maximum_zoom_divisor", ConfigTypes.DOUBLE, 50.0D)
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
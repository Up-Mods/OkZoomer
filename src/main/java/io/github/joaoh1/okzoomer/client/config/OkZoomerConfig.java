package io.github.joaoh1.okzoomer.client.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import me.shedaniel.fiber2cloth.api.ClothAttributes;
import net.minecraft.util.Identifier;

public class OkZoomerConfig {
	public static final Path okZoomerConfigPath = Paths.get("./config/okzoomer-next.json5");

	//TODO - Organize the config in categories
	public static final PropertyMirror<Double> zoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE));
	public static final PropertyMirror<String> cinematicCamera = PropertyMirror.create(ConfigTypes.STRING.withPattern("^off$|^vanilla$|^multiplied$"));
	public static final PropertyMirror<Double> cinematicMultiplier = PropertyMirror.create(ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE));
	public static final PropertyMirror<Boolean> reduceSensitivity = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<String> zoomTransition = PropertyMirror.create(ConfigTypes.STRING.withPattern("^off$|^smooth$|^linear$"));
	public static final PropertyMirror<Boolean> zoomToggle = PropertyMirror.create(ConfigTypes.BOOLEAN);
	public static final PropertyMirror<Boolean> zoomScrolling = PropertyMirror.create(ConfigTypes.BOOLEAN);
	//public static final PropertyMirror<Integer> adjustableZoomSteps = PropertyMirror.create(ConfigTypes.INTEGER.withMinimum(1));
	public static final PropertyMirror<Double> minimumZoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE));
	public static final PropertyMirror<Double> maximumZoomDivisor = PropertyMirror.create(ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE));
	
	public static final ConfigBranch tree = ConfigTree.builder()
		.withAttribute(ClothAttributes.defaultBackground(new Identifier("minecraft:textures/block/yellow_concrete.png")))
		.beginValue("zoom_divisor", ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE), 4.0D)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("The divisor applied to the FOV when zooming.")
		.finishValue(zoomDivisor::mirror)
		.beginValue("cinematic_camera", ConfigTypes.STRING.withPattern("^off$|^vanilla$|^multiplied$"), "off")
			.withAttribute(ClothAttributes.tooltip())
			.withComment("Enables the cinematic camera while zooming.\n\"off\" disables it.\n\"vanilla\" mimics Vanilla's Cinematic Camera.\n\"multiplied\" is a multiplied variant of \"vanilla\".")
		.finishValue(cinematicCamera::mirror)
		.beginValue("cinematic_multiplier", ConfigTypes.DOUBLE, 4.0D)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("The multiplier used on the multiplied cinematic camera.")
		.finishValue(cinematicMultiplier::mirror)
		.beginValue("reduce_sensitivity", ConfigTypes.BOOLEAN, true)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("Reduces the mouse sensitivity when zooming.")
		.finishValue(reduceSensitivity::mirror)
		.beginValue("zoom_transition", ConfigTypes.STRING.withPattern("^off$|^smooth$"), "smooth")
			.withAttribute(ClothAttributes.tooltip())
			.withComment("Adds transitions between zooms.\n\"off\" disables it.\n\"smooth\" starts fast and ends slow.")
		.finishValue(zoomTransition::mirror)
		.beginValue("zoom_toggle", ConfigTypes.BOOLEAN, false)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("Enables the ability to toggle zooming.")
		.finishValue(zoomToggle::mirror)
		.beginValue("zoom_scrolling", ConfigTypes.BOOLEAN, true)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("Allows to increase or decrease zoom by scrolling. Not polished yet.")
		.finishValue(zoomScrolling::mirror)
		/*
		.beginValue("adjustable_zoom_steps", ConfigTypes.INTEGER, 1)
			.withComment("The steps between the default and the maximum zoom divisor.")
		.finishValue(adjustableZoomSteps::mirror)
		*/
		.beginValue("minimum_zoom_divisor", ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE), 1.0D)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("The minimum value that you can scroll down.")
		.finishValue(minimumZoomDivisor::mirror)
		.beginValue("maximum_zoom_divisor", ConfigTypes.DOUBLE.withMinimum(Double.MIN_VALUE), 50.0D)
			.withAttribute(ClothAttributes.tooltip())
			.withComment("The maximum value that you can scroll up.")
		.finishValue(maximumZoomDivisor::mirror)
		.build();

	private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

	public static void loadJanksonConfig() {
		if (Files.exists(okZoomerConfigPath)) {
			try {
				FiberSerialization.deserialize(tree, Files.newInputStream(Paths.get("./config/okzoomer-next.json5")), serializer);
			} catch (IOException | FiberException e) {
				e.printStackTrace();
			}
		} else {
			saveJanksonConfig();
		}
	}

	public static void saveJanksonConfig() {
		try {
			FiberSerialization.serialize(tree, Files.newOutputStream(Paths.get("./config/okzoomer-next.json5")), serializer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
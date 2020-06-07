package io.github.joaoh1.okzoomer.client.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

//TODO - Overhaul the config screen
public class OkZoomerConfigScreen {
	private static String translatedCineOffField = new TranslatableText("config.okzoomer.option.cinematic_camera.off").getString();
	private static String translatedCineVanillaField = new TranslatableText("config.okzoomer.option.cinematic_camera.vanilla").getString();
	private static String translatedCineMultipliedField = new TranslatableText("config.okzoomer.option.cinematic_camera.multiplied").getString();
	private static String translatedTransOffField = new TranslatableText("config.okzoomer.option.zoom_transition.off").getString();
	private static String translatedTransSmoothField = new TranslatableText("config.okzoomer.option.zoom_transition.smooth").getString();
	//private static String translatedTransLinearField = new TranslatableText("config.okzoomer.option.zoom_transition.linear").getString();
	
	private static String getCinematicCameraMode(String value, boolean convertToRegular) {
		if (convertToRegular) {
			if (value.equals(translatedCineOffField)) {
				return "off";
			}
	
			if (value.equals(translatedCineVanillaField)) {
				return "vanilla";
			}
	
			if (value.equals(translatedCineMultipliedField)) {
				return "multiplied";
			}
		} else {
			switch (value) {
				case "off":
					return translatedCineOffField;
				case "vanilla":
					return translatedCineVanillaField;
				case "multiplied":
					return translatedCineMultipliedField;
			}
		}
		return value;
	}

	private static String getZoomTransitionMode(String value, boolean convertToRegular) {
		if (convertToRegular) {
			if (value.equals(translatedTransOffField)) {
				return "off";
			}
	
			if (value.equals(translatedTransSmoothField)) {
				return "smooth";
			}
		} else {
			switch (value) {
				case "off":
					return translatedTransOffField;
				case "smooth":
					return translatedTransSmoothField;
			}
		}
		return value;
	}

    public static Screen getConfigScreen(Screen parentScreen) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/yellow_concrete.png"))
			.setParentScreen(parentScreen)
            .setTitle(new TranslatableText("config.okzoomer.title"));

		builder.setSavingRunnable(() -> {
			OkZoomerConfig.saveJanksonConfig();
		});

		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.general"));
		
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.zoom_divisor"), OkZoomerConfig.zoomDivisor.getValue())
			.setDefaultValue(4.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_divisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomDivisor.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.reduce_sensitivity"), OkZoomerConfig.reduceSensitivity.getValue())
        	.setDefaultValue(true)
        	.setTooltip(new TranslatableText("config.okzoomer.option.reduce_sensitivity.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.reduceSensitivity.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startSelector(
				new TranslatableText("config.okzoomer.option.cinematic_camera"),
				new String[]{
					translatedCineOffField,
					translatedCineVanillaField,
					translatedCineMultipliedField
				},
				getCinematicCameraMode(OkZoomerConfig.cinematicCamera.getValue(), false)
			)
			.setDefaultValue(translatedCineOffField)
			.setTooltip(new TranslatableText("config.okzoomer.option.cinematic_camera.tooltip"))
			.setSaveConsumer(newValue -> {
				OkZoomerConfig.cinematicCamera.setValue(getCinematicCameraMode(newValue, true));
			})
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.cinematic_multiplier"), OkZoomerConfig.cinematicMultiplier.getValue())
			.setDefaultValue(4.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.cinematic_multiplier.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.cinematicMultiplier.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startSelector(
				new TranslatableText("config.okzoomer.option.zoom_transition"),
				new String[]{
					translatedTransOffField,
					translatedTransSmoothField
				},
				getZoomTransitionMode(OkZoomerConfig.zoomTransition.getValue(), false)
			)
			.setDefaultValue(translatedTransSmoothField)
			.setTooltip(new TranslatableText("config.okzoomer.option.zoom_transition.tooltip"))
			.setSaveConsumer(newValue -> {
				OkZoomerConfig.zoomTransition.setValue(getZoomTransitionMode(newValue, true));
			})
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.zoom_toggle"), OkZoomerConfig.zoomToggle.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_toggle.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomToggle.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.zoom_scrolling"), OkZoomerConfig.zoomScrolling.getValue())
        	.setDefaultValue(true)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_scrolling.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomScrolling.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.minimum_zoom_divisor"), OkZoomerConfig.minimumZoomDivisor.getValue())
			.setDefaultValue(1.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.minimum_zoom_divisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.minimumZoomDivisor.setValue(newValue))
			.build());

		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.maximum_zoom_divisor"), OkZoomerConfig.maximumZoomDivisor.getValue())
			.setDefaultValue(50.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.maximum_zoom_divisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.maximumZoomDivisor.setValue(newValue))
			.build());
		
        return builder.build();
    }
}
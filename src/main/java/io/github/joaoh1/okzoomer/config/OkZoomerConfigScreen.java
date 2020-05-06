package io.github.joaoh1.okzoomer.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

//TODO - Overhaul the config screen
public class OkZoomerConfigScreen {
	/*
	private static String getCinematicCameraMode(String value, boolean convertToRegular) {
		String translatedOffField = new TranslatableText("config.okzoomer.option.cinematic_camera.off").asString();
		String translatedVanillaField = new TranslatableText("config.okzoomer.option.cinematic_camera.vanilla").asString();
		String translatedFourXField = new TranslatableText("config.okzoomer.option.cinematic_camera.4x").asString();

		if (convertToRegular) {
			if (value.equals(translatedOffField)) {
				return "off";
			}
	
			if (value.equals(translatedVanillaField)) {
				return "vanilla";
			}
	
			if (value.equals(translatedFourXField)) {
				return "4x";
			}
		} else {
			if (value.equals("off")) {
				return translatedOffField;
			}
			if (value.equals("vanilla")) {
				return translatedVanillaField;
			}
			if (value.equals("4x")) {
				return translatedFourXField;
			}
		}
		return value;
	}
	*/

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
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.reduce_sensitivity"), OkZoomerConfig.reduceSensitivity.getValue())
        	.setDefaultValue(true)
        	.setTooltip(new TranslatableText("config.okzoomer.option.reduce_sensitivity.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.reduceSensitivity.setValue(newValue))
			.build());
		
		/*	
		general.addEntry(entryBuilder.startSelector(
				new TranslatableText("config.okzoomer.option.cinematic_camera"),
				new String[]{
					new TranslatableText("config.okzoomer.option.cinematic_camera.off").getString(),
					new TranslatableText("config.okzoomer.option.cinematic_camera.vanilla").getString(),
					new TranslatableText("config.okzoomer.option.cinematic_camera.4x").getString()
				},
				"Off"
			)
			.setDefaultValue(new TranslatableText("config.okzoomer.option.cinematic_camera.off").getString())
			.setTooltip(new TranslatableText("config.okzoomer.option.cinematic_camera.tooltip"))
			.setSaveConsumer(newValue -> {
				String convertedValue = "4x";
				OkZoomerConfig.cinematicCamera.setValue(convertedValue);
			})
			.build());
		*/
		/*
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.smooth_transition"), OkZoomerConfig.smoothTransition.getValue())
        	.setDefaultValue(true)
        	.setTooltip(new TranslatableText("config.okzoomer.option.smooth_transition.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.smoothTransition.setValue(newValue))
			.build());
		*/
		/*
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.zoom_toggle"), OkZoomerConfig.zoomToggle.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_toggle.tooltip"))
        	//.setSaveConsumer(newValue -> OkZoomerConfig.zoomToggle.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.zoom_divisor"), OkZoomerConfig.zoomDivisor.getValue())
			.setDefaultValue(4.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_divisor.tooltip"))
        	//.setSaveConsumer(newValue -> OkZoomerConfig.zoomDivisor.setValue(newValue))
			.build());
		*/

        return builder.build();
    }
}
package io.github.joaoh1.okzoomer.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class OkZoomerConfigScreen {
	//TODO - Actually make a proper config screen that isn't just a debug menu
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
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.reduce_sensitivity.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.reduceSensitivity.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.cinematic_camera"), OkZoomerConfig.cinematicCamera.getValue())
        	.setDefaultValue(true)
        	.setTooltip(new TranslatableText("config.okzoomer.option.cinematic_camera.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.cinematicCamera.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.use_alternative_smoothing"), OkZoomerConfig.useAlternativeSmoothing.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.use_alternative_smoothing.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.useAlternativeSmoothing.setValue(newValue))
			.build());

		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.zoom_toggle"), OkZoomerConfig.zoomToggle.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_toggle.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomToggle.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.smooth_transition"), OkZoomerConfig.smoothTransition.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.smooth_transition.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.smoothTransition.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.zoom_divisor"), OkZoomerConfig.zoomDivisor.getValue())
			.setDefaultValue(4.0D)
			.setMin(Double.MIN_VALUE)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoom_divisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomDivisor.setValue(newValue))
        	.build());

        return builder.build();
    }
}
package io.github.joaoh1.okzoomer.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class OkZoomerConfigScreen {
	//TODO - Clean up config screen
    public static Screen getConfigScreen(Screen parentScreen) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/yellow_concrete.png"))
			.setParentScreen(parentScreen)
            .setTitle(new TranslatableText("text.okzoomer.config.title"));

		builder.setSavingRunnable(() -> {
			OkZoomerConfig.saveJanksonConfig();
		});

		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("text.okzoomer.config.category.general"));
		
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("text.okzoomer.config.option.zoomToggle"), OkZoomerConfig.zoomToggle.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("text.okzoomer.config.option.zoomToggle.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomToggle.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("text.okzoomer.config.option.zoomDivisor"), OkZoomerConfig.zoomDivisor.getValue())
        	.setDefaultValue(4.0D)
        	.setTooltip(new TranslatableText("text.okzoomer.config.option.zoomDivisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomDivisor.setValue(newValue))
        	.build());

        return builder.build();
    }
}
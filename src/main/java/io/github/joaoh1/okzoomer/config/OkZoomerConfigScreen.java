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
            .setTitle(new TranslatableText("config.okzoomer.title"));

		builder.setSavingRunnable(() -> {
			OkZoomerConfig.saveJanksonConfig();
		});

		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.general"));
		
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.option.zoomToggle"), OkZoomerConfig.zoomToggle.getValue())
        	.setDefaultValue(false)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoomToggle.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomToggle.setValue(newValue))
			.build());
		
		general.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.option.zoomDivisor"), OkZoomerConfig.zoomDivisor.getValue())
        	.setDefaultValue(4.0D)
        	.setTooltip(new TranslatableText("config.okzoomer.option.zoomDivisor.tooltip"))
        	.setSaveConsumer(newValue -> OkZoomerConfig.zoomDivisor.setValue(newValue))
        	.build());

        return builder.build();
    }
}
package io.github.joaoh1.okzoomer.client.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class OkZoomerConfigScreen {
    public static Screen getConfigScreen(Screen parentScreen) {
		ConfigBuilder builder = ConfigBuilder.create()
        	.setParentScreen(parentScreen)
			.setTitle(new TranslatableText("config.okzoomer.title"));
		
		builder.setSavingRunnable(() -> {
			OkZoomerConfig.saveModConfig();
		});

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory features = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.features"));

		features.addEntry(entryBuilder.startSelector(new TranslatableText("config.okzoomer.cinematic_camera"), CinematicCameraOptions.values(), OkZoomerConfigPojo.features.cinematicCamera)
			.setDefaultValue(CinematicCameraOptions.OFF)
			.setNameProvider(value -> {
				if (value.equals(CinematicCameraOptions.OFF)) {
					return new TranslatableText("config.okzoomer.cinematic_camera.off");
				} else if (value.equals(CinematicCameraOptions.VANILLA)) {
					return new TranslatableText("config.okzoomer.cinematic_camera.vanilla");
				} else if (value.equals(CinematicCameraOptions.MULTIPLIED)) {
					return new TranslatableText("config.okzoomer.cinematic_camera.multiplied");
				}
				return new LiteralText("Error");
			})
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.cinematicCamera = (CinematicCameraOptions) value;
			})
			.build());
		
		ConfigCategory values = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.values"));
		
		return builder.build();
    }
}
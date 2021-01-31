package io.github.joaoh1.okzoomer.config;

import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.CinematicCameraOptions;
import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomModes;
import io.github.joaoh1.okzoomer.config.OkZoomerConfigPojo.FeaturesGroup.ZoomTransitionOptions;
import io.github.joaoh1.okzoomer.utils.ZoomUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class OkZoomerConfigScreen {
    public static Screen getConfigScreen(Screen parentScreen) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parentScreen)
			.setDefaultBackgroundTexture(new Identifier("minecraft:textures/block/yellow_concrete.png"))
			.setTitle(new TranslatableText("config.okzoomer.title"));

		builder.setGlobalized(true);
		builder.setGlobalizedExpanded(false);

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
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.cinematic_camera.tooltip"),
				new TranslatableText("config.okzoomer.cinematic_camera.tooltip.off"),
				new TranslatableText("config.okzoomer.cinematic_camera.tooltip.vanilla"),
				new TranslatableText("config.okzoomer.cinematic_camera.tooltip.multiplied")
			})
			.build());
		
		features.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.reduce_sensitivity"), OkZoomerConfigPojo.features.reduceSensitivity)
			.setDefaultValue(true)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.reduceSensitivity = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.reduce_sensitivity.tooltip"))
			.build());
		
		features.addEntry(entryBuilder.startSelector(new TranslatableText("config.okzoomer.zoom_transition"), ZoomTransitionOptions.values(), OkZoomerConfigPojo.features.zoomTransition)
			.setDefaultValue(ZoomTransitionOptions.SMOOTH)
			.setNameProvider(value -> {
				if (value.equals(ZoomTransitionOptions.OFF)) {
					return new TranslatableText("config.okzoomer.zoom_transition.off");
				} else if (value.equals(ZoomTransitionOptions.SMOOTH)) {
					return new TranslatableText("config.okzoomer.zoom_transition.smooth");
				} else if (value.equals(ZoomTransitionOptions.LINEAR)) {
					return new TranslatableText("config.okzoomer.zoom_transition.linear");
				}
				return new LiteralText("Error");
			})
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.zoomTransition = (ZoomTransitionOptions) value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.zoom_transition.tooltip"),
				new TranslatableText("config.okzoomer.zoom_transition.tooltip.off"),
				new TranslatableText("config.okzoomer.zoom_transition.tooltip.smooth"),
				new TranslatableText("config.okzoomer.zoom_transition.tooltip.linear")
			})
			.build());
		
			features.addEntry(entryBuilder.startSelector(new TranslatableText("config.okzoomer.zoom_mode"), ZoomModes.values(), OkZoomerConfigPojo.features.zoomMode)
			.setDefaultValue(ZoomModes.HOLD)
			.setNameProvider(value -> {
				if (value.equals(ZoomModes.HOLD)) {
					return new TranslatableText("options.key.hold");
				} else if (value.equals(ZoomModes.TOGGLE)) {
					return new TranslatableText("options.key.toggle");
				} else if (value.equals(ZoomModes.PERSISTENT)) {
					return new TranslatableText("config.okzoomer.zoom_mode.persistent");
				}
				return new LiteralText("Error");
			})
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.zoomMode = (ZoomModes) value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.zoom_mode.tooltip"))
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.zoom_mode.tooltip"),
				new TranslatableText("config.okzoomer.zoom_mode.tooltip.hold"),
				new TranslatableText("config.okzoomer.zoom_mode.tooltip.toggle"),
				new TranslatableText("config.okzoomer.zoom_mode.tooltip.persistent")
			})
			.build());
		
		features.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.zoom_scrolling"), OkZoomerConfigPojo.features.zoomScrolling)
			.setDefaultValue(true)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.zoomScrolling = value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.zoom_scrolling.tooltip")
			})
			.build());
		
		features.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.extra_keybinds"), OkZoomerConfigPojo.features.extraKeybinds)
			.requireRestart()
			.setDefaultValue(true)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.extraKeybinds = value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.extra_keybinds.tooltip"),
				new TranslatableText("config.okzoomer.extra_keybinds.tooltip.warning")
			})
			.build());
		
		features.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.zoom_overlay"), OkZoomerConfigPojo.features.zoomOverlay)
			.setDefaultValue(false)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.features.zoomOverlay = value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.zoom_overlay.tooltip.1"),
				new TranslatableText("config.okzoomer.zoom_overlay.tooltip.2"),
				new TranslatableText("config.okzoomer.zoom_overlay.tooltip.3")
			})
			.build());
		
		ConfigCategory values = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.values"))
			.setCategoryBackground(new Identifier("minecraft:textures/block/yellow_concrete_powder.png"));

		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.zoom_divisor"), OkZoomerConfigPojo.values.zoomDivisor)
			.setDefaultValue(4.0)
			.setMin(Double.MIN_VALUE)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.zoomDivisor = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.zoom_divisor.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.minimum_zoom_divisor"), OkZoomerConfigPojo.values.minimumZoomDivisor)
			.setDefaultValue(1.0)
			.setMin(Double.MIN_VALUE)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.minimumZoomDivisor = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.minimum_zoom_divisor.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.maximum_zoom_divisor"), OkZoomerConfigPojo.values.maximumZoomDivisor)
			.setDefaultValue(50.0)
			.setMin(Double.MIN_VALUE)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.maximumZoomDivisor = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.maximum_zoom_divisor.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.scroll_step"), OkZoomerConfigPojo.values.scrollStep)
			.setDefaultValue(1.0)
			.setMin(0.0)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.scrollStep = value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.scroll_step.tooltip.1"),
				new TranslatableText("config.okzoomer.scroll_step.tooltip.2")
			})
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.lesser_scroll_step"), OkZoomerConfigPojo.values.lesserScrollStep)
			.setDefaultValue(0.5)
			.setMin(0.0)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.lesserScrollStep = value;
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.lesser_scroll_step.tooltip.1"),
				new TranslatableText("config.okzoomer.lesser_scroll_step.tooltip.2")
			})
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.cinematic_multiplier"), OkZoomerConfigPojo.values.cinematicMultiplier)
			.setDefaultValue(4.0)
			.setMin(Double.MIN_VALUE)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.cinematicMultiplier = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.cinematic_multiplier.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.smooth_multiplier"), OkZoomerConfigPojo.values.smoothMultiplier)
			.setDefaultValue(0.75)
			.setMin(Double.MIN_VALUE)
			.setMax(1.0)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.smoothMultiplier = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.smooth_multiplier.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.minimum_linear_step"), OkZoomerConfigPojo.values.minimumLinearStep)
			.setDefaultValue(0.125)
			.setMin(0)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.minimumLinearStep = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.minimum_linear_step.tooltip"))
			.build());
		
		values.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.okzoomer.maximum_linear_step"), OkZoomerConfigPojo.values.maximumLinearStep)
			.setDefaultValue(0.25)
			.setMin(Double.MIN_VALUE)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.values.maximumLinearStep = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.maximum_linear_step.tooltip"))
			.build());

		ConfigCategory tweaks = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.tweaks"))
			.setCategoryBackground(new Identifier("minecraft:textures/block/yellow_glazed_terracotta.png"));

		tweaks.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.reset_zoom_with_mouse"), OkZoomerConfigPojo.tweaks.resetZoomWithMouse)
			.setDefaultValue(true)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.tweaks.resetZoomWithMouse = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.reset_zoom_with_mouse.tooltip"))
			.build());
		
		tweaks.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.unbind_conflicting_key"), OkZoomerConfigPojo.tweaks.unbindConflictingKey)
			.setDefaultValue(false)
			.setSaveConsumer(value -> {
				if (value.equals(true)) {
					MinecraftClient client = MinecraftClient.getInstance();
					ZoomUtils.unbindConflictingKey(client, true);
				}
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.unbind_conflicting_key.tooltip.1"),
				new TranslatableText("config.okzoomer.unbind_conflicting_key.tooltip.2")
			})
			.build());
		
		tweaks.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.print_owo_on_start"), OkZoomerConfigPojo.tweaks.printOwoOnStart)
			.setDefaultValue(true)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.tweaks.printOwoOnStart = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.print_owo_on_start.tooltip"))
			.build());
		
		tweaks.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.okzoomer.hide_zoom_overlay"), OkZoomerConfigPojo.tweaks.hideZoomOverlay)
			.setDefaultValue(false)
			.setSaveConsumer(value -> {
				OkZoomerConfigPojo.tweaks.hideZoomOverlay = value;
			})
			.setTooltip(new TranslatableText("config.okzoomer.hide_zoom_overlay.tooltip"))
			.build());
		
		ConfigCategory presets = builder.getOrCreateCategory(new TranslatableText("config.okzoomer.category.presets"))
			.setCategoryBackground(new Identifier("minecraft:textures/block/yellow_wool.png"));

		String[] presetArray = new String[]{"None", "Default", "Classic", "Persistent"};
		presets.addEntry(entryBuilder.startSelector(new TranslatableText("config.okzoomer.reset_to_preset"), presetArray, presetArray[0])
			.setSaveConsumer(value -> {
				if (value.equals("Default")) {
					OkZoomerConfigPojo.features.cinematicCamera = CinematicCameraOptions.OFF;
					OkZoomerConfigPojo.features.reduceSensitivity = true;
					OkZoomerConfigPojo.features.zoomTransition = ZoomTransitionOptions.SMOOTH;
					OkZoomerConfigPojo.features.zoomMode = ZoomModes.HOLD;
					OkZoomerConfigPojo.features.zoomScrolling = true;
					OkZoomerConfigPojo.features.extraKeybinds = true;
					OkZoomerConfigPojo.features.zoomOverlay = false;
					OkZoomerConfigPojo.values.zoomDivisor = 4.0;
					OkZoomerConfigPojo.values.minimumZoomDivisor = 1.0;
					OkZoomerConfigPojo.values.maximumZoomDivisor = 50.0;
					OkZoomerConfigPojo.values.scrollStep = 1.0;
					OkZoomerConfigPojo.values.lesserScrollStep = 0.5;
					OkZoomerConfigPojo.values.cinematicMultiplier = 4.0;
					OkZoomerConfigPojo.values.smoothMultiplier = 0.75;
					OkZoomerConfigPojo.values.minimumLinearStep = 0.125;
					OkZoomerConfigPojo.values.maximumLinearStep = 0.25;
					OkZoomerConfigPojo.tweaks.resetZoomWithMouse = true;
					OkZoomerConfigPojo.tweaks.printOwoOnStart = true;
				} else if (value.equals("Classic")) {
					OkZoomerConfigPojo.features.cinematicCamera = CinematicCameraOptions.VANILLA;
					OkZoomerConfigPojo.features.reduceSensitivity = false;
					OkZoomerConfigPojo.features.zoomTransition = ZoomTransitionOptions.OFF;
					OkZoomerConfigPojo.features.zoomMode = ZoomModes.HOLD;
					OkZoomerConfigPojo.features.zoomScrolling = false;
					OkZoomerConfigPojo.features.extraKeybinds = false;
					OkZoomerConfigPojo.features.zoomOverlay = false;
					OkZoomerConfigPojo.values.zoomDivisor = 4.0;
					OkZoomerConfigPojo.values.minimumZoomDivisor = 1.0;
					OkZoomerConfigPojo.values.maximumZoomDivisor = 50.0;
					OkZoomerConfigPojo.values.scrollStep = 1.0;
					OkZoomerConfigPojo.values.lesserScrollStep = 0.5;
					OkZoomerConfigPojo.values.cinematicMultiplier = 4.0;
					OkZoomerConfigPojo.values.smoothMultiplier = 0.75;
					OkZoomerConfigPojo.values.minimumLinearStep = 0.125;
					OkZoomerConfigPojo.values.maximumLinearStep = 0.25;
					OkZoomerConfigPojo.tweaks.resetZoomWithMouse = false;
					OkZoomerConfigPojo.tweaks.printOwoOnStart = true;
				} else if (value.equals("Persistent")) {
					OkZoomerConfigPojo.features.cinematicCamera = CinematicCameraOptions.OFF;
					OkZoomerConfigPojo.features.reduceSensitivity = true;
					OkZoomerConfigPojo.features.zoomTransition = ZoomTransitionOptions.SMOOTH;
					OkZoomerConfigPojo.features.zoomMode = ZoomModes.PERSISTENT;
					OkZoomerConfigPojo.features.zoomScrolling = true;
					OkZoomerConfigPojo.features.extraKeybinds = true;
					OkZoomerConfigPojo.features.zoomOverlay = false;
					OkZoomerConfigPojo.values.zoomDivisor = 1.0;
					OkZoomerConfigPojo.values.minimumZoomDivisor = 1.0;
					OkZoomerConfigPojo.values.maximumZoomDivisor = 50.0;
					OkZoomerConfigPojo.values.scrollStep = 1.0;
					OkZoomerConfigPojo.values.lesserScrollStep = 0.5;
					OkZoomerConfigPojo.values.cinematicMultiplier = 4.0;
					OkZoomerConfigPojo.values.smoothMultiplier = 0.75;
					OkZoomerConfigPojo.values.minimumLinearStep = 0.125;
					OkZoomerConfigPojo.values.maximumLinearStep = 0.25;
					OkZoomerConfigPojo.tweaks.resetZoomWithMouse = true;
					OkZoomerConfigPojo.tweaks.printOwoOnStart = true;
				}
				value = presetArray[0];
			})
			.setNameProvider(value -> {
				if (value.equals("None")) {
					return new TranslatableText("config.okzoomer.reset_to_preset.none");
				} else if (value.equals("Default")) {
					return new TranslatableText("config.okzoomer.reset_to_preset.default");
				} else if (value.equals("Classic")) {
					return new TranslatableText("config.okzoomer.reset_to_preset.classic");
				} else if (value.equals("Persistent")) {
					return new TranslatableText("config.okzoomer.reset_to_preset.persistent");
				}
				return new LiteralText("Error");
			})
			.setTooltip(new TranslatableText[] {
				new TranslatableText("config.okzoomer.reset_to_preset.tooltip"),
				new TranslatableText("config.okzoomer.reset_to_preset.tooltip.none"),
				new TranslatableText("config.okzoomer.reset_to_preset.tooltip.default"),
				new TranslatableText("config.okzoomer.reset_to_preset.tooltip.classic"),
				new TranslatableText("config.okzoomer.reset_to_preset.tooltip.persistent")
			})
			.build());

		builder.setSavingRunnable(() -> {
			OkZoomerConfig.saveModConfig();
		});
		
		return builder.build();
    }
}
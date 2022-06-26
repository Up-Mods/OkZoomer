package io.github.ennuil.ok_zoomer.config.screen;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.api.values.ValueTreeNode;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.option.SpruceCyclingOption;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.CinematicCameraOptions;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ConfigEnum;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.SpyglassDependency;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomModes;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomOverlays;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomPresets;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.screen.widgets.CustomTextureBackground;
import io.github.ennuil.ok_zoomer.config.screen.widgets.SpruceBoundedDoubleInputOption;
import io.github.ennuil.ok_zoomer.config.screen.widgets.SpruceBoundedIntegerInputOption;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OkZoomerConfigScreen extends SpruceScreen {
	private static final CustomTextureBackground NORMAL_BACKGROUND = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_terracotta.png"), 64, 64, 64, 255);
	private static final CustomTextureBackground DARKENED_BACKGROUND = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_terracotta.png"), 32, 32, 32, 255);

	private SpruceOptionListWidget list;
	private final Screen parent;
	private ZoomPresets preset;

	private Map<TrackedValue<Object>, Object> newValues;
	private SpruceOption optionBuffer;

	public OkZoomerConfigScreen(Screen parent) {
		super(Text.translatable("config.ok_zoomer.title"));
		this.parent = parent;
		this.preset = ZoomPresets.DEFAULT;

		this.newValues = new Reference2ObjectArrayMap<>();
		this.optionBuffer = null;
	}

	// Unlike other options, the cycling option doesn't attach the prefix on the text;
	// So we do it ourselves automatically!
	private static Text getCyclingOptionText(String text, Text prefix) {
		return Text.translatable(
			"spruceui.options.generic",
			prefix,
			text != null ? Text.translatable(text) : Text.literal("Error"));
	}

	@Override
	protected void init() {
		super.init();
		this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);
		this.list.setBackground(DARKENED_BACKGROUND);

		this.initializeOptionList(this.list);
		this.appendPresetSection(this.list);

		this.addDrawableChild(this.list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 154, this.height - 28), 150, 20, Text.translatable("config.ok_zoomer.discard_changes"),
			btn -> {
				this.resetNewValues();
				this.refresh();
			}).asVanilla());
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
			btn -> {
				this.newValues.forEach((trackedValue, newValue) -> {
					if (trackedValue.value() != null) {
						trackedValue.setValue(newValue, false);
					}
				});
				OkZoomerConfigManager.CONFIG.save();
				this.client.setScreen(this.parent);
			}).asVanilla());
	}

	@Override
	public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

	@Override
	public void renderBackground(MatrixStack matrices, int vOffset) {
		NORMAL_BACKGROUND.render(matrices, this, vOffset);
	}

	@Override
	public void removed() {
		this.newValues.forEach((trackedValue, newValue) -> trackedValue.setValue(newValue, false));
		OkZoomerConfigManager.CONFIG.save();
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.parent);
	}

	@SuppressWarnings("unchecked")
	private void resetNewValues() {
		this.newValues = new Reference2ObjectArrayMap<>();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			if (trackedValue.getRealValue() != null) {
				newValues.put((TrackedValue<Object>) trackedValue, trackedValue.getRealValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeOptionList(SpruceOptionListWidget options) {
		for (ValueTreeNode node : OkZoomerConfigManager.CONFIG.nodes()) {
			if (node instanceof ValueTreeNode.Section section) {
				var separator = new SpruceSeparatorOption(
					String.format("config.ok_zoomer.%s", section.key()),
					true,
					Text.translatable(String.format("config.ok_zoomer.%s.tooltip", section.key())));
				this.addOptionToList(options, separator, WidgetSize.Size.FULL);

				for (ValueTreeNode subNode : section) {
					WidgetSize.Size size = subNode.metadata(WidgetSize.TYPE);

					if (subNode instanceof TrackedValue<?> trackedValue) {
						var trackie = (TrackedValue<Object>) trackedValue;
						this.newValues.putIfAbsent(trackie, trackedValue.getRealValue());

						if (trackedValue.value() instanceof Boolean) {
							SpruceOption option;
							if (!trackedValue.equals(OkZoomerConfigManager.UNBIND_CONFLICTING_KEY)) {
								option = new SpruceBooleanOption(
									String.format("config.ok_zoomer.%s", trackedValue.key()),
									() -> (Boolean) this.newValues.get(trackie),
									value -> this.newValues.replace(trackie, value),
									Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							} else {
								// TODO - ew, hardcoding; we can do better than that
								option = SpruceSimpleActionOption.of(
									"config.ok_zoomer.tweaks.unbind_conflicting_key",
									button -> ZoomUtils.unbindConflictingKey(client, true),
									Text.translatable("config.ok_zoomer.tweaks.unbind_conflicting_key.tooltip"));
							}
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof Double) {
							double minimum = Double.MIN_VALUE;
							double maximum = Double.MAX_VALUE;
							for (Constraint<?> constraint : trackedValue.constraints()) {
								if (constraint instanceof Constraint.Range<?>) {
									try {
										Field minField = Constraint.Range.class.getDeclaredField("min");
										Field maxField = Constraint.Range.class.getDeclaredField("max");

										minField.setAccessible(true);
										maxField.setAccessible(true);

										minimum = Math.max(((Double) minField.get(constraint)).doubleValue(), minimum);
										maximum = Math.min(((Double) maxField.get(constraint)).doubleValue(), maximum);
									} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
										e.printStackTrace();
									}
								}
							}

							var option = new SpruceBoundedDoubleInputOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								minimum, maximum,
								() -> (Double) this.newValues.get(trackie),
								value -> this.newValues.replace(trackie, value),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof Integer) {
							int minimum = Integer.MIN_VALUE;
							int maximum = Integer.MAX_VALUE;
							for (Constraint<?> constraint : trackedValue.constraints()) {
								if (constraint instanceof Constraint.Range<?>) {
									try {
										Field minField = Constraint.Range.class.getDeclaredField("min");
										Field maxField = Constraint.Range.class.getDeclaredField("max");

										minField.setAccessible(true);
										maxField.setAccessible(true);

										minimum = Math.max(((Integer) minField.get(constraint)).intValue(), minimum);
										maximum = Math.min(((Integer) maxField.get(constraint)).intValue(), maximum);
									} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
										e.printStackTrace();
									}
								}
							}

							var option = new SpruceBoundedIntegerInputOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								minimum, maximum,
								() -> (Integer) this.newValues.get(trackie),
								value -> this.newValues.replace(trackie, value),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof ConfigEnum) {
							var option = new SpruceCyclingOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								amount -> this.newValues.replace(trackie, ((ConfigEnum) this.newValues.get(trackie)).next()),
								option2 -> getCyclingOptionText(String.format("config.ok_zoomer.%s.%s", trackedValue.key(), this.newValues.get(trackie).toString().toLowerCase()), option2.getPrefix()),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						}
					}
				}
			}
		}

		if (this.optionBuffer != null) {
			this.list.addOptionEntry(optionBuffer, null);
			this.optionBuffer = null;
		}
	}

	private void appendPresetSection(SpruceOptionListWidget options) {
		// "Reset" category separator
		var resetSeparator = new SpruceSeparatorOption(
			"config.ok_zoomer.reset",
			true,
			Text.translatable("config.ok_zoomer.reset.tooltip"));

		// Preset
		var presetOption = new SpruceCyclingOption(
			"config.ok_zoomer.reset.preset",
			amount -> this.preset = (ZoomPresets) this.preset.next(),
			option -> getCyclingOptionText(String.format("config.ok_zoomer.reset.preset.%s", this.preset.toString().toLowerCase()), option.getPrefix()),
			Text.translatable("config.ok_zoomer.reset.preset.tooltip"));

		// Reset Settings
		var resetSettingsOption = SpruceSimpleActionOption.of(
			"config.ok_zoomer.reset.reset_settings",
			button -> this.resetToPreset(this.preset),
			Text.translatable("config.ok_zoomer.reset.reset_settings.tooltip"));

		options.addSingleOptionEntry(resetSeparator);
		options.addOptionEntry(presetOption, resetSettingsOption);
	}

	private void addOptionToList(SpruceOptionListWidget options, SpruceOption option, WidgetSize.Size size) {
		if (size == WidgetSize.Size.HALF) {
			if (optionBuffer == null) {
				optionBuffer = option;
			} else {
				this.list.addOptionEntry(optionBuffer, option);
				optionBuffer = null;
			}
		} else {
			if (optionBuffer != null) {
				this.list.addOptionEntry(optionBuffer, null);
				optionBuffer = null;
			}
			this.list.addSingleOptionEntry(option);
		}
	}

	private void refresh() {
		var scrollAmount = this.list.getScrollAmount();
		this.init(client, width, height);
		this.list.setScrollAmount(scrollAmount);
	}

	@SuppressWarnings("unchecked")
	public void resetToPreset(ZoomPresets preset) {
		Map<TrackedValue<?>, Object> presets = Map.ofEntries(
			Map.entry(OkZoomerConfigManager.CINEMATIC_CAMERA, preset == ZoomPresets.CLASSIC ? CinematicCameraOptions.VANILLA : CinematicCameraOptions.OFF),
			Map.entry(OkZoomerConfigManager.REDUCE_SENSITIVITY, preset == ZoomPresets.CLASSIC ? false : true),
			Map.entry(OkZoomerConfigManager.ZOOM_TRANSITION, preset == ZoomPresets.CLASSIC ? ZoomTransitionOptions.OFF : ZoomTransitionOptions.SMOOTH),
			Map.entry(OkZoomerConfigManager.ZOOM_MODE, preset == ZoomPresets.PERSISTENT ? ZoomModes.PERSISTENT : ZoomModes.HOLD),
			Map.entry(OkZoomerConfigManager.ZOOM_SCROLLING, switch (preset) {
				case CLASSIC -> false;
				case SPYGLASS -> false;
				default -> true;
			}),
			Map.entry(OkZoomerConfigManager.EXTRA_KEY_BINDS, preset == ZoomPresets.CLASSIC ? false : true),
			Map.entry(OkZoomerConfigManager.ZOOM_OVERLAY, preset == ZoomPresets.SPYGLASS ? ZoomOverlays.SPYGLASS : ZoomOverlays.OFF),
			Map.entry(OkZoomerConfigManager.SPYGLASS_DEPENDENCY, preset == ZoomPresets.SPYGLASS ? SpyglassDependency.BOTH : SpyglassDependency.OFF),
			Map.entry(OkZoomerConfigManager.ZOOM_DIVISOR, switch (preset) {
				case PERSISTENT -> 1.0D;
				case SPYGLASS -> 10.0D;
				default -> 4.0D;
			}),
			Map.entry(OkZoomerConfigManager.MINIMUM_ZOOM_DIVISOR, 1.0D),
			Map.entry(OkZoomerConfigManager.MAXIMUM_ZOOM_DIVISOR, 50.0D),
			Map.entry(OkZoomerConfigManager.UPPER_SCROLL_STEPS, preset == ZoomPresets.SPYGLASS ? 16 : 20),
			Map.entry(OkZoomerConfigManager.LOWER_SCROLL_STEPS, preset == ZoomPresets.SPYGLASS ? 8 : 4),
			Map.entry(OkZoomerConfigManager.SMOOTH_MULTIPLIER, preset == ZoomPresets.SPYGLASS ? 0.5D : 0.75D),
			Map.entry(OkZoomerConfigManager.CINEMATIC_MULTIPLIER, 4.0D),
			Map.entry(OkZoomerConfigManager.MINIMUM_LINEAR_STEP, 0.125D),
			Map.entry(OkZoomerConfigManager.MAXIMUM_LINEAR_STEP, 0.25D),
			Map.entry(OkZoomerConfigManager.RESET_ZOOM_WITH_MOUSE, preset == ZoomPresets.CLASSIC ? false : true),
			Map.entry(OkZoomerConfigManager.FORGET_ZOOM_DIVISOR, true),
			Map.entry(OkZoomerConfigManager.UNBIND_CONFLICTING_KEY, false),
			Map.entry(OkZoomerConfigManager.USE_SPYGLASS_TEXTURE, preset == ZoomPresets.SPYGLASS ? true : false),
			Map.entry(OkZoomerConfigManager.USE_SPYGLASS_SOUNDS, preset == ZoomPresets.SPYGLASS ? true : false),
			Map.entry(OkZoomerConfigManager.SHOW_RESTRICTION_TOASTS, true),
			Map.entry(OkZoomerConfigManager.PRINT_OWO_ON_START, preset == ZoomPresets.CLASSIC ? false : true)
		);

		this.newValues = new Reference2ObjectArrayMap<>();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			this.newValues.put((TrackedValue<Object>) trackedValue, presets.get(trackedValue));
		}

		this.refresh();
	}
}

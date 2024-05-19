package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.screen.components.LabelledEditBox;
import io.github.ennuil.ok_zoomer.config.screen.components.OkZoomerAbstractSelectionList;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import org.quiltmc.config.api.Configs;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Map;
import java.util.Set;

// TODO - You may have dropped your silly data-driven config screen idea, but you still want to streamline the config screen. Do Config v2!
@ClientOnly
public class OkZoomerConfigScreen extends Screen {
	private final ResourceLocation configId;
	private final Screen parent;
	private ConfigTextUtils configTextUtils;
	private OkZoomerAbstractSelectionList entryListWidget;

	private final Map<TrackedValue<Object>, Object> newValues;
	private final Set<TrackedValue<Object>> invalidValues;
	private AbstractWidget buttonBuffer = null;

	public OkZoomerConfigScreen(Screen parent) {
		super(ConfigTextUtils.getConfigTitle(new ResourceLocation("ok_zoomer", "config")));
		this.configId = new ResourceLocation("ok_zoomer", "config");
		this.parent = parent;
		this.newValues = new Reference2ObjectArrayMap<>();
		this.invalidValues = new ObjectArraySet<>();
	}

	@Override
	protected void init() {
		var config = Configs.getConfig(this.configId.getNamespace(), this.configId.getPath());
		this.configTextUtils = new ConfigTextUtils(config);
		this.entryListWidget = new OkZoomerAbstractSelectionList(this.minecraft, this.width, this.height - 64, 0, 32);
		for (var node : config.nodes()) {
			if (node instanceof ValueTreeNode.Section section) {
				this.entryListWidget.addCategory(this.configTextUtils.getCategoryText(section.key().toString()));

				for (var subNode : section) {
					var size = subNode.metadata(WidgetSize.TYPE);

					if (subNode instanceof TrackedValue<?> trackedValue) {
						var trackie = (TrackedValue<Object>) trackedValue;
						this.newValues.putIfAbsent(trackie, trackedValue.getRealValue());

						if (trackedValue.value() instanceof Boolean) {
							AbstractWidget button;
							if (!trackedValue.equals(OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey)) {
								button = CycleButton.onOffBuilder((Boolean) this.newValues.get(trackie))
									.withTooltip(value -> Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)))
									.create(
										0, 0, 150, 20,
										this.configTextUtils.getOptionText(trackedValue),
										(button_, value) -> this.newValues.replace(trackie, value));
							} else {
								// TODO - ew, hardcoding; we can do better than that
								button = Button.builder(
										Component.translatable("config.ok_zoomer.tweaks.unbind_conflicting_key"),
										button_ -> ZoomUtils.unbindConflictingKey(this.minecraft, true))
									.tooltip(Tooltip.create(Component.translatable("config.ok_zoomer.tweaks.unbind_conflicting_key.tooltip")))
									.build();
							}
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof Double) {
							var button = new LabelledEditBox(
								this.font,
								0, 0, 150, 32,
								this.configTextUtils.getOptionText(trackedValue)
							);
							button.setValue(((Double) this.newValues.get(trackie)).toString());
							button.setResponder(value -> {
								try {
									double min = Double.NEGATIVE_INFINITY;
									double max = Double.POSITIVE_INFINITY;

									for (var constraint : trackedValue.constraints()) {
										if (constraint instanceof Constraint.Range<?> range) {
											min = Math.max(((Constraint.Range<Double>) range).min(), min);
											max = Math.min(((Constraint.Range<Double>) range).max(), max);
										}
									}

									double parsedValue = Double.parseDouble(value);
									if (parsedValue < min || parsedValue > max) {
										// Yes, this isn't exactly right but oh well
										throw new IndexOutOfBoundsException();
									}

									this.newValues.replace(trackie, parsedValue);
									this.invalidValues.remove(trackie);
									button.setTextColor(0xFFE0E0E0);
								} catch (NumberFormatException | IndexOutOfBoundsException e) {
									this.invalidValues.add(trackie);
									button.setTextColor(CommonColors.RED);
								}
							});
							button.setTooltip(Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)));
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof Integer) {
							var button = new LabelledEditBox(
								this.font,
								0, 0, 150, 32,
								this.configTextUtils.getOptionText(trackedValue)
							);
							button.setValue(((Integer) this.newValues.get(trackie)).toString());
							button.setResponder(value -> {
								try {
									int min = Integer.MIN_VALUE;
									int max = Integer.MAX_VALUE;

									for (var constraint : trackedValue.constraints()) {
										if (constraint instanceof Constraint.Range<?> range) {
											min = Math.max(((Constraint.Range<Integer>) range).min(), min);
											max = Math.min(((Constraint.Range<Integer>) range).max(), max);
										}
									}

									int parsedValue = Integer.parseInt(value);
									if (parsedValue < min || parsedValue > max) {
										// Yes, this isn't exactly right but oh well
										throw new IndexOutOfBoundsException();
									}

									this.newValues.replace(trackie, parsedValue);
									this.invalidValues.remove(trackie);
									button.setTextColor(0xFFE0E0E0);
								} catch (NumberFormatException | IndexOutOfBoundsException e) {
									this.invalidValues.add(trackie);
									button.setTextColor(CommonColors.RED);
								}
							});
							button.setTooltip(Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)));
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof ConfigEnums.ConfigEnum configEnum) {
							var button = CycleButton.<ConfigEnums.ConfigEnum>builder(value -> this.configTextUtils.getEnumOptionText(trackedValue, value))
								.withValues((ConfigEnums.ConfigEnum[]) ((Enum<?>) configEnum).getDeclaringClass().getEnumConstants())
								.withTooltip(value -> Tooltip.create(this.configTextUtils.getEnumOptionTextTooltip(trackedValue, value)))
								.withInitialValue((ConfigEnums.ConfigEnum) this.newValues.get(trackie))
								.create(
									0, 0, 150, 20,
									this.configTextUtils.getOptionText(trackedValue),
									(button_, value) -> this.newValues.replace(trackie, value));
							this.addOptionToList(button, size);
						}
					}
				}

				if (this.buttonBuffer != null) {
					this.entryListWidget.addButton(buttonBuffer, null);
					this.buttonBuffer = null;
				}
			}
		}

		this.entryListWidget.addCategory(Component.translatable("config.ok_zoomer.presets"));
		var presetButton = CycleButton.<ConfigEnums.ZoomPresets>builder(value -> Component.translatable(String.format("config.ok_zoomer.presets.preset.%s", value.toString().toLowerCase())))
			.withValues(ConfigEnums.ZoomPresets.values())
			.withTooltip(value -> Tooltip.create(Component.translatable(String.format("config.ok_zoomer.presets.preset.%s.tooltip", value.toString().toLowerCase()))))
			.withInitialValue(ConfigEnums.ZoomPresets.DEFAULT)
			.create(0, 0, 150, 20,
				Component.translatable("config.ok_zoomer.presets.preset"));
		var resetButton = Button.builder(
				Component.translatable("config.ok_zoomer.presets.reset_settings"),
				button -> this.resetToPreset(presetButton.getValue()))
			.tooltip(Tooltip.create(Component.translatable("config.ok_zoomer.presets.reset_settings.tooltip")))
			.build();
		this.entryListWidget.addButton(presetButton, resetButton);

		this.entryListWidget.finish();
		this.addRenderableWidget(this.entryListWidget);

		this.addRenderableWidget(
			Button.builder(Component.translatable("config.ok_zoomer.discard_changes"), button -> this.resetNewValues())
				.bounds(this.width / 2 - 155, this.height - 27, 150, 20)
				.build());

		this.addRenderableWidget(
			Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.parent))
				.bounds(this.width / 2 + 5, this.height - 27, 150, 20)
				.build());
	}

	private void addOptionToList(AbstractWidget button, WidgetSize.Size size) {
		if (size == WidgetSize.Size.HALF) {
			if (this.buttonBuffer == null) {
				this.buttonBuffer = button;
			} else {
				this.entryListWidget.addButton(this.buttonBuffer, button);
				this.buttonBuffer = null;
			}
		} else {
			if (this.buttonBuffer != null) {
				this.entryListWidget.addButton(this.buttonBuffer, null);
				this.buttonBuffer = null;
			}
			this.entryListWidget.addButton(button);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics, mouseX, mouseY, delta);
		// Y: 20 is technically the vanilla Y, but I'd rather go for as close to 1.20.5 vanilla Y as possible
		graphics.drawCenteredString(this.font, ConfigTextUtils.getConfigTitle(configId), this.width / 2, 15, CommonColors.WHITE);
		this.entryListWidget.render(graphics, mouseX, mouseY, delta);
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}

	@Override
	public void removed() {
		this.newValues.forEach((trackedValue, newValue) -> {
			if (!invalidValues.contains(trackedValue)) {
				trackedValue.setValue(newValue, false);
			}
		});
		OkZoomerConfigManager.CONFIG.save();
	}

	private void refresh() {
		var scrollAmount = this.entryListWidget.getScrollAmount();
		this.repositionElements();
		this.entryListWidget.setScrollAmount(scrollAmount);
	}

	private void resetNewValues() {
		this.newValues.clear();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			if (trackedValue.getRealValue() != null) {
				newValues.put((TrackedValue<Object>) trackedValue, trackedValue.getRealValue());
			}
		}

		this.refresh();
	}

	@SuppressWarnings("unchecked")
	public void resetToPreset(ConfigEnums.ZoomPresets preset) {
		Map<TrackedValue<?>, Object> presets = Map.ofEntries(
				Map.entry(OkZoomerConfigManager.CONFIG.features.cinematicCamera, preset == ConfigEnums.ZoomPresets.CLASSIC ? ConfigEnums.CinematicCameraOptions.VANILLA : ConfigEnums.CinematicCameraOptions.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.features.reduceSensitivity, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoomTransition, preset == ConfigEnums.ZoomPresets.CLASSIC ? ConfigEnums.ZoomTransitionOptions.OFF : ConfigEnums.ZoomTransitionOptions.SMOOTH),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoomMode, preset == ConfigEnums.ZoomPresets.PERSISTENT ? ConfigEnums.ZoomModes.PERSISTENT : ConfigEnums.ZoomModes.HOLD),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoomScrolling, switch (preset) {
					case CLASSIC, SPYGLASS -> false;
					default -> true;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.features.extraKeyBinds, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoomOverlay, preset == ConfigEnums.ZoomPresets.SPYGLASS ? ConfigEnums.ZoomOverlays.SPYGLASS : ConfigEnums.ZoomOverlays.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.features.spyglassMode, preset == ConfigEnums.ZoomPresets.SPYGLASS ? ConfigEnums.SpyglassMode.BOTH : ConfigEnums.SpyglassMode.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.zoomDivisor, switch (preset) {
					case PERSISTENT -> 1.0D;
					case SPYGLASS -> 10.0D;
					default -> 4.0D;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.minimumZoomDivisor, 1.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.maximumZoomDivisor, 50.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.upperScrollSteps, switch (preset) {
					case PERSISTENT -> 38;
					case SPYGLASS -> 16;
					default -> 20;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.lowerScrollSteps, switch (preset) {
					case PERSISTENT -> 0;
					case SPYGLASS -> 8;
					default -> 4;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.transitionValues.smoothTransitionFactor, switch (preset) {
					case CLASSIC_ZOOMER -> 0.75;
					case SPYGLASS -> 0.5;
					default -> 0.6;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.zoomValues.cinematicMultiplier, 4.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.transitionValues.minimumLinearStep, 0.125D),
				Map.entry(OkZoomerConfigManager.CONFIG.transitionValues.maximumLinearStep, 0.25D),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.resetZoomWithMouse, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.forgetZoomDivisor, true),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey, false),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.useSpyglassTexture, preset == ConfigEnums.ZoomPresets.SPYGLASS),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.useSpyglassSounds, preset == ConfigEnums.ZoomPresets.SPYGLASS),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.showRestrictionToasts, true),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.printOwoOnStart, false)
		);

		this.newValues.clear();
		this.invalidValues.clear();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			this.newValues.put((TrackedValue<Object>) trackedValue, presets.get(trackedValue));
		}

		this.refresh();
	}
}

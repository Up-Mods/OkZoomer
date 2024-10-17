package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.config.screen.components.LabelledEditBox;
import io.github.ennuil.ok_zoomer.config.screen.components.OkZoomerAbstractSelectionList;
import io.github.ennuil.ok_zoomer.utils.ModUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

// TODO - You may have dropped your silly data-driven config screen idea, but you still want to streamline the config screen. Do Config v2!
@ClientOnly
public class OkZoomerConfigScreen extends Screen {
	private final ResourceLocation configId;
	private final Screen parent;
	private ConfigTextUtils configTextUtils;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private OkZoomerAbstractSelectionList entryListWidget;

	private final Map<TrackedValue<Object>, Object> newValues;
	private final Set<TrackedValue<Object>> invalidValues;
	private AbstractWidget buttonBuffer = null;

	public OkZoomerConfigScreen(Screen parent) {
		super(ConfigTextUtils.getConfigTitle(ModUtils.id("config")));
		this.configId = ModUtils.id("config");
		this.parent = parent;
		this.newValues = new Reference2ObjectArrayMap<>();
		this.invalidValues = new ObjectArraySet<>();
	}

	@Override
	protected void init() {
		var config = Configs.getConfig(this.configId.getNamespace(), this.configId.getPath());
		this.configTextUtils = new ConfigTextUtils(config);
		this.entryListWidget = new OkZoomerAbstractSelectionList(this.minecraft, this.width, this.height - 64, 32);

		this.entryListWidget.addCategory(Component.translatable("config.ok_zoomer.presets"));
		var presetButton = CycleButton.<ConfigEnums.ZoomPresets>builder(value -> Component.translatable(String.format("config.ok_zoomer.presets.preset.%s", value.toString().toLowerCase(Locale.ROOT))))
			.withValues(ConfigEnums.ZoomPresets.values())
			.withTooltip(value -> Tooltip.create(Component.translatable(String.format("config.ok_zoomer.presets.preset.%s.tooltip", value.toString().toLowerCase(Locale.ROOT)))))
			.withInitialValue(ConfigEnums.ZoomPresets.CAMERA)
			.create(0, 0, 150, 20,
				Component.translatable("config.ok_zoomer.presets.preset"));
		var resetButton = Button.builder(
				Component.translatable("config.ok_zoomer.presets.apply_preset"),
				button -> this.resetToPreset(presetButton.getValue()))
			.tooltip(Tooltip.create(Component.translatable("config.ok_zoomer.presets.apply_preset.tooltip")))
			.build();
		this.entryListWidget.addButton(presetButton, resetButton);

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

		this.entryListWidget.finish();
		this.addRenderableWidget(this.entryListWidget);

		this.layout.addTitleHeader(this.title, this.font);
		var footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
		footerLayout.addChild(Button.builder(Component.translatable("config.ok_zoomer.discard_changes"), button -> this.resetNewValues()).width(150).build());
		footerLayout.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(150).build());

		this.layout.visitWidgets(this::addRenderableWidget);
		this.repositionElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		this.entryListWidget.updateSize(this.width, this.layout);
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
		this.rebuildWidgets();
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
		this.newValues.clear();
		this.invalidValues.clear();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			this.newValues.put(
				(TrackedValue<Object>) trackedValue,
				ZoomPresets.PRESET_ENUM_TO_PRESET.get(preset).getOrDefault(trackedValue, trackedValue.getDefaultValue())
			);
		}

		this.refresh();
	}
}

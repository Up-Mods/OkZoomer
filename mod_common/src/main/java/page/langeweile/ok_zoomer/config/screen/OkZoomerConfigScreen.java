package page.langeweile.ok_zoomer.config.screen;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
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
import page.langeweile.ok_zoomer.config.ConfigEnums;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;
import page.langeweile.ok_zoomer.config.metadata.WidgetSize;
import page.langeweile.ok_zoomer.config.screen.components.OkZoomerFloatSlider;
import page.langeweile.ok_zoomer.config.screen.components.OkZoomerIntegerSlider;
import page.langeweile.ok_zoomer.config.screen.components.OkZoomerSelectionList;
import page.langeweile.ok_zoomer.utils.ModUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

// TODO - You may have dropped your silly data-driven config screen idea, but you still want to streamline the config screen. Do Config v2!
public class OkZoomerConfigScreen extends Screen {
	private final ResourceLocation configId;
	private final Screen parent;
	private ConfigTextUtils configTextUtils;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private OkZoomerSelectionList selectionList;

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
		this.selectionList = new OkZoomerSelectionList(this.minecraft, this.width, this.height - 64, 32, this);

		this.selectionList.addCategory(Component.translatable("config.ok_zoomer.presets"));
		var presetButton = CycleButton.<ConfigEnums.ZoomPresets>builder(
			value -> Component.translatable(String.format("config.ok_zoomer.presets.preset.%s", value.toString().toLowerCase(Locale.ROOT)))
			)
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
		this.selectionList.addButton(presetButton, resetButton);

		for (var node : config.nodes()) {
			if (node instanceof ValueTreeNode.Section section) {
				this.selectionList.addCategory(this.configTextUtils.getCategoryText(section.key().toString()));

				for (var subNode : section) {
					var size = subNode.metadata(WidgetSize.TYPE);

					if (subNode instanceof TrackedValue<?> trackedValue) {
						var trackie = (TrackedValue<Object>) trackedValue;
						this.newValues.putIfAbsent(trackie, trackedValue.getRealValue());

						if (trackedValue.value() instanceof Boolean) {
							if (trackedValue.equals(OkZoomerConfigManager.CONFIG.tweaks.unbindConflictingKey)) {
								continue;
							}

							var button = CycleButton.onOffBuilder((Boolean) this.newValues.get(trackie))
								.withTooltip(value -> Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)))
								.create(
									0, 0, 150, 20,
									this.configTextUtils.getOptionText(trackedValue),
									(button_, value) -> this.newValues.replace(trackie, value));
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof Float) {
							if (OkZoomerConfigManager.CONFIG.tweaks.numericSliders.value()) {
								var slider = new OkZoomerFloatSlider(
									(TrackedValue<Float>) (Object) trackie,
									this.configTextUtils.getOptionText(trackie),
									0, 0, 150, 20,
									(float) this.newValues.get(trackie),
									value -> this.newValues.replace(trackie, value)
								);
								slider.setTooltip(Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)));
								this.addOptionToList(slider, size);
							} else {
								var button = new EditBox(
									this.font,
									0, 0, 150, 20,
									this.configTextUtils.getOptionText(trackedValue)
								);
								button.setValue(((Float) this.newValues.get(trackie)).toString());
								button.setResponder(value -> {
									try {
										float min = Float.NEGATIVE_INFINITY;
										float max = Float.POSITIVE_INFINITY;

										for (var constraint : trackedValue.constraints()) {
											if (constraint instanceof Constraint.Range<?> range) {
												min = Math.max(((Constraint.Range<Float>) range).min(), min);
												max = Math.min(((Constraint.Range<Float>) range).max(), max);
											}
										}

										float parsedValue = Float.parseFloat(value);
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
								button.setTooltip(Tooltip.create(
									CommonComponents.joinLines(
										this.configTextUtils.getOptionText(trackedValue),
										this.configTextUtils.getOptionTextTooltip(trackedValue)
									)
								));
								this.addOptionToList(button, size);
							}
						} else if (trackedValue.value() instanceof Integer) {
							if (OkZoomerConfigManager.CONFIG.tweaks.numericSliders.value()) {
								var slider = new OkZoomerIntegerSlider(
									(TrackedValue<Integer>) (Object) trackie,
									this.configTextUtils.getOptionText(trackie),
									0, 0, 150, 20,
									(int) this.newValues.get(trackie),
									value -> this.newValues.replace(trackie, value)
								);
								slider.setTooltip(Tooltip.create(this.configTextUtils.getOptionTextTooltip(trackedValue)));
								this.addOptionToList(slider, size);
							} else {
								var button = new EditBox(
									this.font,
									0, 0, 150, 20,
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
								button.setTooltip(Tooltip.create(
									CommonComponents.joinLines(
										this.configTextUtils.getOptionText(trackedValue),
										this.configTextUtils.getOptionTextTooltip(trackedValue)
									)
								));
								this.addOptionToList(button, size);
							}
						} else if (trackedValue.value() instanceof ConfigEnums.ConfigEnum configEnum) {
							var button = CycleButton.<ConfigEnums.ConfigEnum>builder(
								value -> this.configTextUtils.getEnumOptionText(trackedValue, value)
								)
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
					this.selectionList.addButton(buttonBuffer, null);
					this.buttonBuffer = null;
				}
			}
		}

		this.addWidget(this.selectionList);

		this.addRenderableWidget(
			Button.builder(Component.translatable("config.ok_zoomer.discard_changes"), button -> this.resetNewValues())
				.bounds(this.width / 2 - 155, this.height - 27, 150, 20)
				.build());

		this.addRenderableWidget(
			Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.parent))
				.bounds(this.width / 2 + 5, this.height - 27, 150, 20)
				.build());
	}

	@Override
	public void renderBackground(GuiGraphics graphics) {
		super.renderDirtBackground(graphics);
	}

	private void addOptionToList(AbstractWidget button, WidgetSize.Size size) {
		if (size == WidgetSize.Size.HALF) {
			if (this.buttonBuffer == null) {
				this.buttonBuffer = button;
			} else {
				this.selectionList.addButton(this.buttonBuffer, button);
				this.buttonBuffer = null;
			}
		} else {
			if (this.buttonBuffer != null) {
				this.selectionList.addButton(this.buttonBuffer, null);
				this.buttonBuffer = null;
			}
			this.selectionList.addButton(button);
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(graphics);
		// Y: 20 is technically the vanilla Y, but I'd rather go for as close to 1.20.5 vanilla Y as possible
		graphics.drawCenteredString(this.font, ConfigTextUtils.getConfigTitle(configId), this.width / 2, 15, CommonColors.WHITE);
		this.selectionList.render(graphics, mouseX, mouseY, partialTick);
		super.render(graphics, mouseX, mouseY, partialTick);
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
		var scrollAmount = this.selectionList.getScrollAmount();
		this.rebuildWidgets();
		this.selectionList.setScrollAmount(scrollAmount);
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

package io.github.ennuil.ok_zoomer.config.screen;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Identifier;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Configs;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.List;
import java.util.Map;

// TODO - You may have dropped your silly data-driven config screen idea, but you still want to streamline the config screen. Do Config v2!
@ClientOnly
public class OkZoomerConfigScreen extends Screen {
	private final Identifier configId;
	private final Screen parent;
	private Config config;
	private OkZoomerEntryListWidget entryListWidget;

	private final Map<TrackedValue<Object>, Object> newValues;
	private final List<TrackedValue<Object>> invalidValues;
	private ClickableWidget buttonBuffer = null;

	public OkZoomerConfigScreen(Screen parent) {
		super(ConfigTextUtils.getConfigTitle(new Identifier("ok_zoomer", "config")));
		this.configId = new Identifier("ok_zoomer", "config");
		this.parent = parent;
		this.newValues = new Reference2ObjectArrayMap<>();
		this.invalidValues = new ObjectArrayList<>();
	}

	@Override
	protected void init() {
		this.config = Configs.getConfig(this.configId.getNamespace(), this.configId.getPath());
		this.addDrawableChild(
			ButtonWidget.builder(CommonTexts.DONE, button -> this.client.setScreen(parent))
				.positionAndSize(this.width / 2 - 100, this.height - 27, 200, 20)
				.build());
		this.entryListWidget = new OkZoomerEntryListWidget(this.client, this.width, this.height - 64, 0, 32);
		for (var node : this.config.nodes()) {
			if (node instanceof ValueTreeNode.Section section) {
				this.entryListWidget.addCategory(ConfigTextUtils.getCategoryText(this.configId, section.key().toString()));

				for (var subNode : section) {
					var size = subNode.metadata(WidgetSize.TYPE);

					if (subNode instanceof TrackedValue<?> trackedValue) {
						var trackie = (TrackedValue<Object>) trackedValue;
						this.newValues.putIfAbsent(trackie, trackedValue.getRealValue());

						if (trackedValue.value() instanceof Boolean) {
							ClickableWidget button;
							if (!trackedValue.equals(OkZoomerConfigManager.CONFIG.tweaks.unbind_conflicting_key)) {
								button = CyclingButtonWidget.onOffBuilder((Boolean) this.newValues.get(trackie))
									.tooltip(value -> Tooltip.create(Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key()))))
									.build(
										0, 0, 150, 20,
										Text.translatable(String.format("config.ok_zoomer.%s", trackedValue.key())),
										(button_, value) -> this.newValues.replace(trackie, value));
							} else {
								// TODO - ew, hardcoding; we can do better than that
								button = ButtonWidget.builder(
									Text.translatable("config.ok_zoomer.tweaks.unbind_conflicting_key"),
									button_ -> ZoomUtils.unbindConflictingKey(this.client, true))
									.tooltip(Tooltip.create(Text.translatable("config.ok_zoomer.tweaks.unbind_conflicting_key.tooltip")))
									.build();
							}
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof Double) {
							// TODO - This was just a prototype to get text fields working; Do Better!
							var button = new TextFieldWidget(
									this.textRenderer,
									0, 0, 150, 20,
									Text.translatable(String.format("config.ok_zoomer.%s", trackedValue.key()))
							);
							button.setText(((Double) this.newValues.get(trackie)).toString());
							button.setChangedListener(value -> {
								try {
									this.newValues.replace(trackie, Double.parseDouble(value));
									this.invalidValues.remove(trackie);
									button.setEditableColor(0xFFE0E0E0);
								} catch (NumberFormatException e) {
									this.invalidValues.add(trackie);
									button.setEditableColor(CommonColors.RED);
								}
							});
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof Integer) {
							var button = new TextFieldWidget(
									this.textRenderer,
									0, 0, 150, 20,
									Text.translatable(String.format("config.ok_zoomer.%s", trackedValue.key()))
							);
							button.setText(((Integer) this.newValues.get(trackie)).toString());
							button.setChangedListener(value -> {
								try {
									this.newValues.replace(trackie, Integer.parseInt(value));
									this.invalidValues.remove(trackie);
									button.setEditableColor(0xFFE0E0E0);
								} catch (NumberFormatException e) {
									this.invalidValues.add(trackie);
									button.setEditableColor(CommonColors.RED);
								}
							});
							this.addOptionToList(button, size);
						} else if (trackedValue.value() instanceof ConfigEnums.ConfigEnum configEnum) {
							var button = CyclingButtonWidget.<ConfigEnums.ConfigEnum>builder(value -> Text.translatable(String.format("config.ok_zoomer.%s.%s", trackedValue.key(), value.toString().toLowerCase())))
								.values((ConfigEnums.ConfigEnum[]) ((Enum<?>) configEnum).getDeclaringClass().getEnumConstants())
								.tooltip(value -> Tooltip.create(Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key()))))
								.initially((ConfigEnums.ConfigEnum) this.newValues.get(trackie))
								.build(
									0, 0, 150, 20,
									Text.translatable(String.format("config.ok_zoomer.%s", trackedValue.key())),
									(button_, value) -> {
										System.out.println(value);
										this.newValues.replace(trackie, value);
									});
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

		this.entryListWidget.addCategory(Text.translatable("config.ok_zoomer.reset"));
		var presetButton = CyclingButtonWidget.<ConfigEnums.ZoomPresets>builder(value -> Text.translatable(String.format("config.ok_zoomer.reset.preset.%s", value.toString().toLowerCase())))
			.values(ConfigEnums.ZoomPresets.values())
			.tooltip(value -> Tooltip.create(Text.translatable("config.ok_zoomer.reset.preset.tooltip")))
			.initially(ConfigEnums.ZoomPresets.DEFAULT)
			.build(0, 0, 150, 20,
					Text.translatable("config.ok_zoomer.reset.preset"));
		var resetButton = ButtonWidget.builder(
				Text.translatable("config.ok_zoomer.reset.reset_settings"),
				button -> this.resetToPreset(presetButton.getValue()))
				.tooltip(Tooltip.create(Text.translatable("config.ok_zoomer.reset.reset_settings.tooltip")))
				.build();
		this.entryListWidget.addButton(presetButton, resetButton);

		this.entryListWidget.finish();
		this.addSelectableChild(entryListWidget);
	}

	private void addOptionToList(ClickableWidget button, WidgetSize.Size size) {
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
		this.renderBackground(graphics);
		// Y: 20 is technically the vanilla Y, but I'd rather go for as close to 1.20.5 vanilla Y as possible
		graphics.drawCenteredShadowedText(this.textRenderer, ConfigTextUtils.getConfigTitle(configId), this.width / 2, 15, CommonColors.WHITE);
		this.entryListWidget.render(graphics, mouseX, mouseY, delta);
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.parent);
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
		this.init(this.client, this.width, this.height);
		this.entryListWidget.setScrollAmount(scrollAmount);
	}

	@SuppressWarnings("unchecked")
	public void resetToPreset(ConfigEnums.ZoomPresets preset) {
		Map<TrackedValue<?>, Object> presets = Map.ofEntries(
				Map.entry(OkZoomerConfigManager.CONFIG.features.cinematic_camera, preset == ConfigEnums.ZoomPresets.CLASSIC ? ConfigEnums.CinematicCameraOptions.VANILLA : ConfigEnums.CinematicCameraOptions.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.features.reduce_sensitivity, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoom_transition, preset == ConfigEnums.ZoomPresets.CLASSIC ? ConfigEnums.ZoomTransitionOptions.OFF : ConfigEnums.ZoomTransitionOptions.SMOOTH),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoom_mode, preset == ConfigEnums.ZoomPresets.PERSISTENT ? ConfigEnums.ZoomModes.PERSISTENT : ConfigEnums.ZoomModes.HOLD),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoom_scrolling, switch (preset) {
					case CLASSIC -> false;
					case SPYGLASS -> false;
					default -> true;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.features.extra_key_binds, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.features.zoom_overlay, preset == ConfigEnums.ZoomPresets.SPYGLASS ? ConfigEnums.ZoomOverlays.SPYGLASS : ConfigEnums.ZoomOverlays.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.features.spyglass_dependency, preset == ConfigEnums.ZoomPresets.SPYGLASS ? ConfigEnums.SpyglassDependency.BOTH : ConfigEnums.SpyglassDependency.OFF),
				Map.entry(OkZoomerConfigManager.CONFIG.values.zoom_divisor, switch (preset) {
					case PERSISTENT -> 1.0D;
					case SPYGLASS -> 10.0D;
					default -> 4.0D;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.values.minimum_zoom_divisor, 1.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.values.maximum_zoom_divisor, 50.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.values.upper_scroll_steps, switch (preset) {
					case PERSISTENT -> 38;
					case SPYGLASS -> 16;
					default -> 20;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.values.lower_scroll_steps, switch (preset) {
					case PERSISTENT -> 0;
					case SPYGLASS -> 8;
					default -> 4;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.values.smooth_multiplier, switch (preset) {
					case CLASSIC_ZOOMER -> 0.75;
					case SPYGLASS -> 0.5;
					default -> 0.6;
				}),
				Map.entry(OkZoomerConfigManager.CONFIG.values.cinematic_multiplier, 4.0D),
				Map.entry(OkZoomerConfigManager.CONFIG.values.minimum_linear_step, 0.125D),
				Map.entry(OkZoomerConfigManager.CONFIG.values.maximum_linear_step, 0.25D),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.reset_zoom_with_mouse, preset != ConfigEnums.ZoomPresets.CLASSIC),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.forget_zoom_divisor, true),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.unbind_conflicting_key, false),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.use_spyglass_texture, preset == ConfigEnums.ZoomPresets.SPYGLASS),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.use_spyglass_sounds, preset == ConfigEnums.ZoomPresets.SPYGLASS),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.show_restriction_toasts, true),
				Map.entry(OkZoomerConfigManager.CONFIG.tweaks.print_owo_on_start, false)
		);

		this.newValues.clear();
		this.invalidValues.clear();

		for (TrackedValue<?> trackedValue : OkZoomerConfigManager.CONFIG.values()) {
			this.newValues.put((TrackedValue<Object>) trackedValue, presets.get(trackedValue));
		}

		this.refresh();
	}
}

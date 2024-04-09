package io.github.ennuil.ok_zoomer.config.screen.v2;

import io.github.ennuil.ok_zoomer.config.ConfigEnums;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.metadata.WidgetSize;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Identifier;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Configs;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.Map;

@ClientOnly
public class OkZoomerConfigScreen extends Screen {
	private final Identifier configId;
	private final Screen parent;
	private Config config;
	private OkZoomerEntryListWidget entryListWidget;

	private final Map<TrackedValue<Object>, Object> newValues;
	private ClickableWidget buttonBuffer = null;

	public OkZoomerConfigScreen(Screen parent) {
		super(ConfigTextUtils.getConfigTitle(new Identifier("ok_zoomer", "config")));
		this.configId = new Identifier("ok_zoomer", "config");
		this.parent = new io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen(parent);
		this.newValues = new Reference2ObjectArrayMap<>();
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
		this.newValues.forEach((trackedValue, newValue) -> trackedValue.setValue(newValue, false));
		OkZoomerConfigManager.CONFIG.save();
	}
}

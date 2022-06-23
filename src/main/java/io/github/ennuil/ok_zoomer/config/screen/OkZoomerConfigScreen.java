package io.github.ennuil.ok_zoomer.config.screen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.values.TrackedValue;
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
import io.github.ennuil.ok_zoomer.config.WidgetSize;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager.ZoomPresets;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// TODO - Manual labor sucks, let's make the machine do our bidding
public class OkZoomerConfigScreen extends SpruceScreen {
	private final Screen parent;
	private SpruceOptionListWidget list;
	private ZoomPresets preset;
	private CustomTextureBackground normalBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_concrete.png"), 64, 64, 64, 255);
	private CustomTextureBackground darkenedBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/yellow_concrete.png"), 32, 32, 32, 255);

	private SpruceOption optionBuffer;

	public OkZoomerConfigScreen(Screen parent) {
		super(Text.translatable("config.ok_zoomer.title"));
		this.parent = parent;
		this.preset = ZoomPresets.DEFAULT;
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
		this.list.setBackground(darkenedBackground);

		this.initializeOptionList(this.list);

		/*
		// Unbind Conflicting Key
		var unbindConflictingKeyOption = SpruceSimpleActionOption.of(
			"config.ok_zoomer.unbind_conflicting_key",
			button -> ZoomUtils.unbindConflictingKey(client, true),
			Text.translatable("config.ok_zoomer.unbind_conflicting_key.tooltip"));
		*/

		this.addDrawableChild(this.list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 154, this.height - 28), 150, 20, Text.translatable("config.ok_zoomer.discard_changes"),
			btn -> {
				// TODO - This small thing points out to the need to overhaul the way the config screen works
				//OkZoomerConfigManager.loadModConfig();
				double scrollAmount = this.list.getScrollAmount();
				this.init(client, width, height);
				this.list.setScrollAmount(scrollAmount);
			}).asVanilla());
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
			btn -> {
				this.client.setScreen(this.parent);
			}).asVanilla());
	}

	@Override
	public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

	@Override
	public void renderBackground(MatrixStack matrices, int vOffset) {
		normalBackground.render(matrices, this, vOffset);
	}

	@Override
	public void removed() {
		//OkZoomerConfigManager.saveModConfig();
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.parent);
	}

	private void initializeOptionList(SpruceOptionListWidget options) {
		for (ValueTreeNode node : OkZoomerConfigManager.CONFIG.nodes()) {
			//System.out.println(node.getClass());
			if (node instanceof ValueTreeNode.Section section) {
				var separator = new SpruceSeparatorOption(
					String.format("config.ok_zoomer.%s", section.key()),
					true,
					Text.translatable(String.format("config.ok_zoomer.%s.tooltip", section.key())));
				this.addOptionToList(options, separator, WidgetSize.Size.FULL);

				for (ValueTreeNode subNode : section) {
					WidgetSize.Size size = subNode.metadata(WidgetSize.TYPE);

					if (subNode instanceof TrackedValue<?> trackedValue) {
						if (trackedValue.value() instanceof Boolean) {
							var option = new SpruceBooleanOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								() -> (Boolean) trackedValue.value(),
								value -> ((TrackedValue<Boolean>) trackedValue).setValue(value, true),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof Double) {
							var option = new SpruceBoundedDoubleInputOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								20.0, Optional.of(0.0), Optional.empty(),
								() -> (Double) trackedValue.value(),
								value -> ((TrackedValue<Double>) trackedValue).setValue(value, true),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof Integer) {
							// TODO - Replace me with less hacky shenanigans!
							int minimum = Integer.MIN_VALUE;
							int maximum = Integer.MAX_VALUE;
							for (Constraint<?> constraint : trackedValue.constraints()) {
								if (constraint instanceof Constraint.Range<?> range) {
									// FIXME - Yeah, this is broken
									try {
										range.getClass().getField("min").trySetAccessible();
										range.getClass().getField("max").trySetAccessible();

										minimum = ((Integer) range.getClass().getField("min").get(range)).intValue();
										maximum = ((Integer) range.getClass().getField("max").get(range)).intValue();

										range.getClass().getField("min").setAccessible(false);
										range.getClass().getField("max").setAccessible(false);
									} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
										e.printStackTrace();
									}
								}
							}

							var option = new SpruceBoundedIntegerInputOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								20, Optional.of(minimum), Optional.of(maximum),
								() -> (Integer) trackedValue.value(),
								value -> ((TrackedValue<Integer>) trackedValue).setValue(value, true),
								Text.translatable(String.format("config.ok_zoomer.%s.tooltip", trackedValue.key())));
							this.addOptionToList(options, option, size);
						} else if (trackedValue.value() instanceof Enum) {
							var classEnum = trackedValue.value().getClass().getEnumConstants();
							var option = new SpruceCyclingOption(
								String.format("config.ok_zoomer.%s", trackedValue.key()),
								amount -> ((TrackedValue<Enum<?>>) trackedValue).setValue((Enum<?>) classEnum[((Enum<?>) trackedValue.value()).ordinal() + 1 < classEnum.length ? ((Enum<?>) trackedValue.value()).ordinal() + 1 : 0], true),
								option2 -> getCyclingOptionText(String.format("config.ok_zoomer.%s.%s", trackedValue.key(), trackedValue.value().toString().toLowerCase()), option2.getPrefix()),
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
}

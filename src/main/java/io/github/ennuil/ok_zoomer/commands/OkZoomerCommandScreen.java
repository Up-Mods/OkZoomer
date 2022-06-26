package io.github.ennuil.ok_zoomer.commands;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.background.SimpleColorBackground;
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.config.screen.widgets.SpruceLabelOption;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class OkZoomerCommandScreen extends SpruceScreen {
	private SpruceOptionListWidget list;
	private SimpleColorBackground darkenedBackground = new SimpleColorBackground(0, 0, 0, 128);

	public OkZoomerCommandScreen() {
		super(Text.translatable("command.ok_zoomer.title"));
	}

	@Override
	protected void init() {
		super.init();
		this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);

		var configButton = SpruceSimpleActionOption.of(
			"command.ok_zoomer.config",
			button -> this.client.setScreen(new OkZoomerConfigScreen(this)),
			null);

		var restrictionsSeparator = new SpruceSeparatorOption(
			"command.ok_zoomer.restrictions",
			true,
			Text.translatable("command.ok_zoomer.restrictions.tooltip"));

		this.list.addSingleOptionEntry(configButton);
		this.list.addSingleOptionEntry(restrictionsSeparator);

		boolean acknowledged = false;

		if (ZoomPackets.getHasRestrictions()) {
			var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.acknowledgement", true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getDisableZoom()) {
			var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.disable_zoom", true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getDisableZoomScrolling()) {
			var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.disable_zoom_scrolling", true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getForceClassicMode()) {
			var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.force_classic_mode", true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getForceZoomDivisors()) {
			double minimumZoomDivisor = ZoomPackets.getMinimumZoomDivisor();
			double maximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
			var textLabel = new SpruceLabelOption(
				"command.ok_zoomer.restrictions.force_zoom_divisors",
				minimumZoomDivisor != maximumZoomDivisor
					? Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisors", minimumZoomDivisor, maximumZoomDivisor)
					: Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisor", minimumZoomDivisor),
				true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getSpyglassDependency()) {
			String key = switch (OkZoomerConfigManager.SPYGLASS_DEPENDENCY.value()) {
				case REQUIRE_ITEM -> "command.ok_zoomer.restrictions.force_spyglass.require_item";
				case REPLACE_ZOOM -> "command.ok_zoomer.restrictions.force_spyglass.replace_zoom";
				case BOTH -> "command.ok_zoomer.restrictions.force_spyglass.both";
				default -> "";
			};
			var textLabel = new SpruceLabelOption(key, true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (ZoomPackets.getSpyglassOverlay()) {
			var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.force_spyglass_overlay", true);
			this.list.addSingleOptionEntry(textLabel);
		}

		if (!ZoomPackets.getHasRestrictions()) {
			if (acknowledged) {
				var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.no_restrictions.acknowledged", true);
				this.list.addSingleOptionEntry(textLabel);
			} else {
				var textLabel = new SpruceLabelOption("command.ok_zoomer.restrictions.no_restrictions", true);
				this.list.addSingleOptionEntry(textLabel);
			}
		}

		this.list.setBackground(darkenedBackground);

		this.addDrawableChild(this.list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 100, this.height - 28), 200, 20, SpruceTexts.GUI_DONE,
			btn -> this.client.setScreen(null)).asVanilla());
	}

	@Override
	public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}
}

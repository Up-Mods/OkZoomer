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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.text.Text;

public class OkZoomerCommandScreen extends SpruceScreen {
	private static final SimpleColorBackground DARKENED_BACKGROUND = new SimpleColorBackground(0, 0, 0, 128);

	public OkZoomerCommandScreen() {
		super(Text.translatable("command.ok_zoomer.title"));
	}

	@Override
	protected void init() {
		super.init();
		var list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);
		list.setBackground(DARKENED_BACKGROUND);

		var configButton = SpruceSimpleActionOption.of(
			"command.ok_zoomer.config",
			button -> this.client.setScreen(new OkZoomerConfigScreen(this)),
			null);

		var restrictionsSeparator = new SpruceSeparatorOption(
			"command.ok_zoomer.restrictions",
			true,
			Text.translatable("command.ok_zoomer.restrictions.tooltip"));

		list.addSingleOptionEntry(configButton);
		list.addSingleOptionEntry(restrictionsSeparator);


		if (ZoomPackets.getHasRestrictions()) {
			list.addSingleOptionEntry(new SpruceLabelOption("command.ok_zoomer.restrictions.acknowledgement", true));
		}

		if (ZoomPackets.getDisableZoom()) {
			list.addSingleOptionEntry(new SpruceLabelOption("command.ok_zoomer.restrictions.disable_zoom", true));
		}

		if (ZoomPackets.getDisableZoomScrolling()) {
			list.addSingleOptionEntry(new SpruceLabelOption("command.ok_zoomer.restrictions.disable_zoom_scrolling", true));
		}

		if (ZoomPackets.getForceClassicMode()) {
			list.addSingleOptionEntry(new SpruceLabelOption("command.ok_zoomer.restrictions.force_classic_mode", true));
		}

		if (ZoomPackets.getForceZoomDivisors()) {
			double minimumZoomDivisor = ZoomPackets.getMinimumZoomDivisor();
			double maximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
			list.addSingleOptionEntry(new SpruceLabelOption(
				"command.ok_zoomer.restrictions.force_zoom_divisors",
				minimumZoomDivisor != maximumZoomDivisor
					? Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisors", minimumZoomDivisor, maximumZoomDivisor)
					: Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisor", minimumZoomDivisor),
				true)
			);
		}

		if (ZoomPackets.getSpyglassDependency()) {
			var key = switch (OkZoomerConfigManager.CONFIG.features.spyglass_dependency.value()) {
				case REQUIRE_ITEM -> "command.ok_zoomer.restrictions.force_spyglass.require_item";
				case REPLACE_ZOOM -> "command.ok_zoomer.restrictions.force_spyglass.replace_zoom";
				case BOTH -> "command.ok_zoomer.restrictions.force_spyglass.both";
				default -> "";
			};
			list.addSingleOptionEntry(new SpruceLabelOption(key, true));
		}

		if (ZoomPackets.getSpyglassOverlay()) {
			list.addSingleOptionEntry(new SpruceLabelOption("command.ok_zoomer.restrictions.force_spyglass_overlay", true));
		}

		if (!ZoomPackets.getHasRestrictions()) {
			boolean acknowledged = ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_NO_RESTRICTIONS);
			list.addSingleOptionEntry(new SpruceLabelOption(acknowledged
				? "command.ok_zoomer.restrictions.no_restrictions.acknowledged"
				: "command.ok_zoomer.restrictions.no_restrictions",
				true)
			);
		}

		this.addDrawableChild(list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 100, this.height - 28), 200, 20, SpruceTexts.GUI_DONE,
			btn -> this.client.setScreen(null)).asVanilla());
	}

	@Override
	public void renderTitle(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.drawCenteredShadowedText(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}
}

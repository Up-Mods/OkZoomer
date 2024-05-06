package io.github.ennuil.ok_zoomer.commands;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerEntryListWidget;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.CommonColors;

public class OkZoomerCommandScreen extends Screen {
	private OkZoomerEntryListWidget entryListWidget;

	public OkZoomerCommandScreen() {
		super(Text.translatable("command.ok_zoomer.title"));
	}

	@Override
	protected void init() {
		this.entryListWidget = new OkZoomerEntryListWidget(this.client, this.width, this.height - 64, 0, 32);
		this.entryListWidget.addButton(
			ButtonWidget.builder(
				Text.translatable("command.ok_zoomer.config"),
				button -> this.client.setScreen(new OkZoomerConfigScreen(this))
			)
			.build()
		);
		this.entryListWidget.addCategory(Text.translatable("command.ok_zoomer.restrictions"));

		if (ZoomPackets.hasRestrictions()) {
			this.entryListWidget.addServerEffectEntry(Text.translatable("command.ok_zoomer.restrictions.acknowledgement"));
		}

		if (ZoomPackets.shouldDisableZoom()) {
			this.entryListWidget.addServerEffectEntry(Text.translatable("command.ok_zoomer.restrictions.disable_zoom"));
		}

		if (ZoomPackets.shouldDisableZoomScrolling()) {
			this.entryListWidget.addServerEffectEntry(Text.translatable("command.ok_zoomer.restrictions.disable_zoom_scrolling"));
		}

		if (ZoomPackets.shouldForceClassicMode()) {
			this.entryListWidget.addServerEffectEntry(Text.translatable("command.ok_zoomer.restrictions.force_classic_mode"));
		}

		if (ZoomPackets.shouldForceZoomDivisors()) {
			double minimumZoomDivisor = ZoomPackets.getMinimumZoomDivisor();
			double maximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
			this.entryListWidget.addServerEffectEntry(minimumZoomDivisor != maximumZoomDivisor
					? Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisors", minimumZoomDivisor, maximumZoomDivisor)
					: Text.translatable("command.ok_zoomer.restrictions.force_zoom_divisor", minimumZoomDivisor));
		}

		if (ZoomPackets.shouldForceSpyglassMode()) {
			var text = switch (OkZoomerConfigManager.CONFIG.features.spyglass_mode.value()) {
				case REQUIRE_ITEM -> Text.translatable("command.ok_zoomer.restrictions.force_spyglass.require_item");
				case REPLACE_ZOOM -> Text.translatable("command.ok_zoomer.restrictions.force_spyglass.replace_zoom");
				case BOTH -> Text.translatable("command.ok_zoomer.restrictions.force_spyglass.both");
				default -> CommonTexts.EMPTY;
			};
			this.entryListWidget.addServerEffectEntry(text);
		}

		if (ZoomPackets.shouldForceSpyglassOverlay()) {
			this.entryListWidget.addServerEffectEntry(Text.translatable("command.ok_zoomer.restrictions.force_spyglass_overlay"));
		}

		if (!ZoomPackets.hasRestrictions()) {
			boolean acknowledged = ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_NO_RESTRICTIONS);
			this.entryListWidget.addServerEffectEntry(Text.translatable(acknowledged
				? "command.ok_zoomer.restrictions.no_restrictions.acknowledged"
				: "command.ok_zoomer.restrictions.no_restrictions"));
		}

		this.entryListWidget.finish();
		this.addDrawableChild(this.entryListWidget);

		this.addDrawableChild(
			ButtonWidget.builder(CommonTexts.DONE, button -> this.closeScreen())
				.positionAndSize(this.width / 2 - 100, this.height - 27, 200, 20)
				.build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics);
		graphics.drawCenteredShadowedText(this.textRenderer, this.getTitle(), this.width / 2, 15, CommonColors.WHITE);
		this.entryListWidget.render(graphics, mouseX, mouseY, delta);
		super.render(graphics, mouseX, mouseY, delta);
	}
}

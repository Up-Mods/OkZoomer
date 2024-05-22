package io.github.ennuil.ok_zoomer.commands;

import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen;
import io.github.ennuil.ok_zoomer.config.screen.components.OkZoomerAbstractSelectionList;
import io.github.ennuil.ok_zoomer.packets.ZoomPackets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class OkZoomerCommandScreen extends Screen {
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private OkZoomerAbstractSelectionList entryListWidget;

	public OkZoomerCommandScreen() {
		super(Component.translatable("command.ok_zoomer.title"));
	}

	@Override
	protected void init() {
		this.entryListWidget = new OkZoomerAbstractSelectionList(this.minecraft, this.width, this.height - 64, 32);
		this.entryListWidget.addButton(
			Button.builder(
				Component.translatable("command.ok_zoomer.config"),
				button -> this.minecraft.setScreen(new OkZoomerConfigScreen(this))
			)
			.build()
		);
		this.entryListWidget.addCategory(Component.translatable("command.ok_zoomer.restrictions"));

		if (ZoomPackets.hasRestrictions()) {
			this.entryListWidget.addServerEffectEntry(Component.translatable("command.ok_zoomer.restrictions.acknowledgement"));
		}

		if (ZoomPackets.shouldDisableZoom()) {
			this.entryListWidget.addServerEffectEntry(Component.translatable("command.ok_zoomer.restrictions.disable_zoom"));
		}

		if (ZoomPackets.shouldDisableZoomScrolling()) {
			this.entryListWidget.addServerEffectEntry(Component.translatable("command.ok_zoomer.restrictions.disable_zoom_scrolling"));
		}

		if (ZoomPackets.shouldForceClassicMode()) {
			this.entryListWidget.addServerEffectEntry(Component.translatable("command.ok_zoomer.restrictions.force_classic_mode"));
		}

		if (ZoomPackets.shouldForceZoomDivisors()) {
			double minimumZoomDivisor = ZoomPackets.getMinimumZoomDivisor();
			double maximumZoomDivisor = ZoomPackets.getMaximumZoomDivisor();
			this.entryListWidget.addServerEffectEntry(minimumZoomDivisor != maximumZoomDivisor
					? Component.translatable("command.ok_zoomer.restrictions.force_zoom_divisors", minimumZoomDivisor, maximumZoomDivisor)
					: Component.translatable("command.ok_zoomer.restrictions.force_zoom_divisor", minimumZoomDivisor));
		}

		if (ZoomPackets.shouldForceSpyglassMode()) {
			var text = switch (OkZoomerConfigManager.CONFIG.features.spyglassMode.value()) {
				case REQUIRE_ITEM -> Component.translatable("command.ok_zoomer.restrictions.force_spyglass.require_item");
				case REPLACE_ZOOM -> Component.translatable("command.ok_zoomer.restrictions.force_spyglass.replace_zoom");
				case BOTH -> Component.translatable("command.ok_zoomer.restrictions.force_spyglass.both");
				default -> CommonComponents.EMPTY;
			};
			this.entryListWidget.addServerEffectEntry(text);
		}

		if (ZoomPackets.shouldForceSpyglassOverlay()) {
			this.entryListWidget.addServerEffectEntry(Component.translatable("command.ok_zoomer.restrictions.force_spyglass_overlay"));
		}

		if (!ZoomPackets.hasRestrictions()) {
			boolean acknowledged = ZoomPackets.getAcknowledgement().equals(ZoomPackets.Acknowledgement.HAS_NO_RESTRICTIONS);
			this.entryListWidget.addServerEffectEntry(Component.translatable(acknowledged
				? "command.ok_zoomer.restrictions.no_restrictions.acknowledged"
				: "command.ok_zoomer.restrictions.no_restrictions"));
		}

		this.entryListWidget.finish();
		this.addRenderableWidget(this.entryListWidget);

		this.layout.addTitleHeader(this.title, this.font);
		this.layout.addToFooter(
			Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
				.bounds(this.width / 2 - 100, this.height - 27, 200, 20)
				.build());
		this.layout.visitWidgets(this::addRenderableWidget);
		this.layout.arrangeElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		this.entryListWidget.updateSize(this.width, this.layout);
	}
}

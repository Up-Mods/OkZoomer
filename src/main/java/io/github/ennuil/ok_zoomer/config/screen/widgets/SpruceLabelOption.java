package io.github.ennuil.ok_zoomer.config.screen.widgets;


import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.network.chat.Component;

public class SpruceLabelOption extends SpruceOption {
	private final Component text;
	private final boolean centered;

	public SpruceLabelOption(String key, boolean centered) {
		this(key, Component.translatable(key), centered);
	}

	public SpruceLabelOption(String key, Component text, boolean centered) {
		super(key);
		this.text = text;
		this.centered = centered;
	}

	@Override
	public SpruceWidget createWidget(Position position, int width) {
		var label = new SpruceLabelWidget(position, this.text, width, this.centered);
		this.getOptionTooltip().ifPresent(label::setTooltip);
		return label;
	}
}

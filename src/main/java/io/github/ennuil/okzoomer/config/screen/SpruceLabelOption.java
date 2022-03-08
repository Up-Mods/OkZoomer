package io.github.ennuil.okzoomer.config.screen;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class SpruceLabelOption extends SpruceOption {
    private final Text text;
    private final boolean centered;

    public SpruceLabelOption(String key, boolean centered) {
        this(key, new TranslatableText(key), centered);
    }

    public SpruceLabelOption(String key, Text text, boolean centered) {
        super(key);
        this.text = text;
        this.centered = centered;
    }

    @Override
    public SpruceWidget createWidget(Position position, int width) {
        var label = new SpruceLabelWidget(position, this.text, width, this.centered);
        return label;
    }
}

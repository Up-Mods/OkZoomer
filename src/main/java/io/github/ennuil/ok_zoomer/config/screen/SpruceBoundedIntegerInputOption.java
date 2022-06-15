package io.github.ennuil.ok_zoomer.config.screen;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceNamedTextFieldWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SpruceBoundedIntegerInputOption extends SpruceOption {
	private final Supplier<Integer> getter;
	private final Consumer<Integer> setter;
	private final Text tooltip;
	private final int defaultValue;
	private final Optional<Integer> minimum;
	private final Optional<Integer> maximum;

	public SpruceBoundedIntegerInputOption(String key, int defaultValue, Optional<Integer> minimum, Optional<Integer> maximum, Supplier<Integer> getter, Consumer<Integer> setter, @Nullable Text tooltip) {
		super(key);
		this.defaultValue = defaultValue;
		this.minimum = minimum;
		this.maximum = maximum;
		this.getter = getter;
		this.setter = setter;
		this.tooltip = tooltip;
	}

	@Override
	public SpruceWidget createWidget(Position position, int width) {
		var textField = new SpruceTextFieldWidget(position, width, 20, this.getPrefix());
		textField.setText(String.valueOf(this.get()));
		textField.setTextPredicate(SpruceTextFieldWidget.INTEGER_INPUT_PREDICATE);
		textField.setRenderTextProvider((displayedText, offset) -> {
			try {
				MutableText tooltipText = Text.empty().append(this.tooltip);
				Style tooltipStyle = Style.EMPTY;
				int value = Integer.parseInt(textField.getText());
				Optional<Boolean> bound = boundCheck(value);
				if (bound.isPresent()) {
					tooltipStyle = tooltipStyle.withColor(Formatting.RED);
					if (minimum.isPresent()) {
						if (!bound.get()) {
							tooltipText = tooltipText.append("\n");
							tooltipText = tooltipText.append(Text.translatable(
								"config.ok_zoomer.widget.bounded_int.below_range",
								minimum.get().toString()
							).setStyle(tooltipStyle));
						} else {
							tooltipText = tooltipText.append(Text.translatable(
								"config.ok_zoomer.widget.bounded_int.above_range",
								maximum.get().toString()
							).setStyle(tooltipStyle));
						}
					}
				}
				textField.setTooltip(tooltipText);
				return OrderedText.method_30747(displayedText, tooltipStyle);
			} catch (NumberFormatException e) {
				return OrderedText.method_30747(displayedText, Style.EMPTY.withColor(Formatting.RED));
			}
		});
		textField.setChangedListener(input -> {
			int value;
			try {
				value = Integer.parseInt(textField.getText());
				if (boundCheck(value).isPresent()) {
					value = this.defaultValue;
				}
			} catch (NumberFormatException e) {
				value = this.defaultValue;
			}
			this.set(value);
		});
		this.setTooltip(this.tooltip);
		return new SpruceNamedTextFieldWidget(textField);
	}

	public void set(int value) {
		this.setter.accept(value);
	}

	public int get() {
		return this.getter.get();
	}

	private Optional<Boolean> boundCheck(int value) {
		if (minimum.isPresent() && value < minimum.get()) {
			return Optional.of(false);
		}
		if (maximum.isPresent() && value > maximum.get()) {
			return Optional.of(true);
		}
		return Optional.empty();
	}
}

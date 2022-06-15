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

public class SpruceBoundedDoubleInputOption extends SpruceOption {
	private final Supplier<Double> getter;
	private final Consumer<Double> setter;
	private final Text tooltip;
	private final double defaultValue;
	private final Optional<Double> minimum;
	private final Optional<Double> maximum;

	public SpruceBoundedDoubleInputOption(String key, double defaultValue, Optional<Double> minimum, Optional<Double> maximum, Supplier<Double> getter, Consumer<Double> setter, @Nullable Text tooltip) {
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
		textField.setTextPredicate(SpruceTextFieldWidget.DOUBLE_INPUT_PREDICATE);
		textField.setRenderTextProvider((displayedText, offset) -> {
			try {
				MutableText tooltipText = Text.empty().append(this.tooltip);
				Style tooltipStyle = Style.EMPTY;
				double value = Double.parseDouble(textField.getText());
				Optional<Boolean> bound = boundCheck(value);
				if (bound.isPresent()) {
					tooltipStyle = tooltipStyle.withColor(Formatting.RED);
					if (minimum.isPresent()) {
						if (!bound.get()) {
							boolean aboveZero = minimum.get() == Double.MIN_NORMAL;
							tooltipText = tooltipText.append("\n");
							tooltipText = tooltipText.append(Text.translatable(
								"config.ok_zoomer.widget.bounded_double.below_range",
								aboveZero ? Text.translatable("config.ok_zoomer.widget.bounded_double.above_zero") : minimum.get().toString()
							).setStyle(tooltipStyle));
						} else {
							tooltipText = tooltipText.append(Text.translatable(
								"config.ok_zoomer.widget.bounded_double.above_range",
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
			double value;
			try {
				value = Double.parseDouble(textField.getText());
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

	public void set(double value) {
		this.setter.accept(value);
	}

	public double get() {
		return this.getter.get();
	}

	private Optional<Boolean> boundCheck(double value) {
		if (minimum.isPresent() && value < minimum.get()) {
			return Optional.of(false);
		}
		if (maximum.isPresent() && value > maximum.get()) {
			return Optional.of(true);
		}
		return Optional.empty();
	}
}

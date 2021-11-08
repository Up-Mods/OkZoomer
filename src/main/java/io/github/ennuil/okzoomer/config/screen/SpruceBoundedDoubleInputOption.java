package io.github.ennuil.okzoomer.config.screen;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceNamedTextFieldWidget;
import dev.lambdaurora.spruceui.widget.text.SpruceTextFieldWidget;
import net.minecraft.text.LiteralText;
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
                double value = Double.parseDouble(textField.getText());
                boundCheck(value);
                textField.setTooltip(this.tooltip);
                return OrderedText.styledForwardsVisitedString(displayedText, Style.EMPTY);
            } catch (NumberFormatException | OutOfBoundsException e) {
                if (e instanceof OutOfBoundsException) {
                    // TODO - Clean this up, make it translatable
                    MutableText tooltipText = new LiteralText("").append(this.tooltip);
                    if (minimum.isPresent() && Double.parseDouble(textField.getText()) < minimum.get()) {
                        String minimumText = minimum.get().toString();
                        if (minimum.get() == Double.MIN_NORMAL) {
                            minimumText = "above 0";
                        }
                        tooltipText = tooltipText.append(new LiteralText("\nThe number is below the allowed range! The minimum is " + minimumText + "!").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    }
                    if (maximum.isPresent() && Double.parseDouble(textField.getText()) < maximum.get()) {
                        tooltipText = tooltipText.append(new LiteralText("\nThe number is above the allowed range! The maximum is " + maximum.get() + "!").setStyle(Style.EMPTY.withColor(Formatting.RED)));
                    }
                    textField.setTooltip(tooltipText);
                }
                return OrderedText.styledForwardsVisitedString(displayedText, Style.EMPTY.withColor(Formatting.RED));
            }
        });
        textField.setChangedListener(input -> {
            double value;
            try {
                value = Double.parseDouble(textField.getText());
                boundCheck(value);
            } catch (NumberFormatException | OutOfBoundsException e) {
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

    private void boundCheck(Double value) throws OutOfBoundsException {
        if (minimum.isPresent() && value < minimum.get()) {
            throw new OutOfBoundsException("The number is below the allowed range!");
        }
        if (maximum.isPresent() && value > maximum.get()) {
            throw new OutOfBoundsException("The number is above the allowed range!");
        }
    }

    private class OutOfBoundsException extends Exception {
        public OutOfBoundsException(String string) {
            super(string);
        }
    }
}

package page.langeweile.ok_zoomer.config.screen.components;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.quiltmc.config.api.values.TrackedValue;
import page.langeweile.ok_zoomer.config.metadata.RangeSubset;

import java.util.function.Consumer;

public class OkZoomerIntegerSlider extends AbstractSliderButton {
	private final Component optionText;
	private final int minValue;
	private final int maxValue;
	private final Consumer<Integer> responder;
	private int internalValue;

	public OkZoomerIntegerSlider(TrackedValue<Integer> trackedValue, Component optionText, int x, int y, int width, int height, int value, Consumer<Integer> responder) {
		super(x, y, width, height, CommonComponents.optionNameValue(
			optionText,
			Component.literal(String.valueOf(value))
		), 0.0);
		this.optionText = optionText;
		this.responder = responder;
		this.internalValue = value;

		int minValue = 0;
		int maxValue = 100;

		if (trackedValue.hasMetadata(RangeSubset.TYPE)) {
			var range = trackedValue.metadata(RangeSubset.TYPE);
			minValue = range.min();
			maxValue = range.max();
		}

		this.minValue = minValue;
		this.maxValue = maxValue;

		this.value = (double) Mth.clamp(value, minValue, maxValue) / (maxValue - minValue);
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(CommonComponents.optionNameValue(
			optionText,
			Component.literal(String.valueOf(this.internalValue))
		));
	}

	@Override
	protected void applyValue() {
		int value = Mth.floor(Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), this.minValue, this.maxValue));
		this.responder.accept(value);
		this.internalValue = value;
	}
}

package io.github.ennuil.ok_zoomer.zoom.modifiers;

//A sin was probably committed by using a lot of for each loops
/**
 * A mouse modifier that contains multiple mouse modifiers.
 */
public class ContainingMouseModifier implements MouseModifier {
	private final MouseModifier[] modifiers;
	private boolean active;

	/**
	 * Initializes an instance of the containing mouse modifier
	 *
	 * @param modifiers the contained mouse modifiers
	*/
	public ContainingMouseModifier(MouseModifier... modifiers) {
		this.modifiers = modifiers;
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		double returnedValue = cursorDeltaX;
		for (MouseModifier modifier : modifiers) {
			returnedValue = modifier.applyXModifier(returnedValue, cursorSensitivity, mouseUpdateTimeDelta, targetDivisor, transitionMultiplier);
		}

		return returnedValue;
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		double returnedValue = cursorDeltaY;
		for (MouseModifier modifier : modifiers) {
			returnedValue = modifier.applyYModifier(returnedValue, cursorSensitivity, mouseUpdateTimeDelta, targetDivisor, transitionMultiplier);
		}

		return returnedValue;
	}

	@Override
	public void tick(boolean active) {
		boolean generalActive = false;
		for (MouseModifier modifier : modifiers) {
			if (modifier == null) continue;

			modifier.tick(active);

			if (!generalActive) {
				generalActive = modifier.getActive();
			}
		}
		this.active = generalActive;
	}
}

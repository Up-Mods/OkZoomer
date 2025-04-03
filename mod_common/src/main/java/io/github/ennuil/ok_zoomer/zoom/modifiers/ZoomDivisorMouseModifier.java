package io.github.ennuil.ok_zoomer.zoom.modifiers;

/**
 * A mouse modifier which reduces the cursor sensitivity with the transition mode's internal multiplier
 */
public class ZoomDivisorMouseModifier implements MouseModifier {
	private boolean active;

	/**
	 * Initializes an instance of the zoom divisor mouse modifier.
	*/
	public ZoomDivisorMouseModifier() {
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		return cursorDeltaX * (this.active ? transitionMultiplier : 1.0);
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		return cursorDeltaY * (this.active ? transitionMultiplier : 1.0);
	}

	@Override
	public void tick(boolean active) {
		this.active = active;
	}
}

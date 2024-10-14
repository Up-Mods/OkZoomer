package io.github.ennuil.ok_zoomer.zoom.modifiers;

/**
 * An implementation of the spyglass' reduction of the mouse sensitivity as a mouse modifier
*/
public class SpyglassMouseModifier implements MouseModifier {
	private boolean active;

	/**
	 * Initializes an instance of the spyglass mouse modifier.
	*/
	public SpyglassMouseModifier() {
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		return cursorDeltaX / (this.active ? 8.0 : 1.0);
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		return cursorDeltaY / (this.active ? 8.0 : 1.0);
	}

	@Override
	public void tick(boolean active) {
		this.active = active;
	}
}

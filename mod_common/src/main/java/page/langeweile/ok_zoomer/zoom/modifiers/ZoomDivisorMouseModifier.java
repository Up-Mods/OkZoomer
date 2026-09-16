package page.langeweile.ok_zoomer.zoom.modifiers;

public class ZoomDivisorMouseModifier implements MouseModifier {
	private boolean active;

	public ZoomDivisorMouseModifier() {
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		return cursorDeltaX * transitionMultiplier;
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		return cursorDeltaY * transitionMultiplier;
	}

	@Override
	public void tick(boolean active, boolean transitionActive) {
		this.active = transitionActive;
	}
}

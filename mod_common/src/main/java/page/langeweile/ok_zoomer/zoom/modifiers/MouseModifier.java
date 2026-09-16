package page.langeweile.ok_zoomer.zoom.modifiers;

public interface MouseModifier {
	boolean getActive();

	double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier);

	double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier);

	void tick(boolean active, boolean transitionActive);
}

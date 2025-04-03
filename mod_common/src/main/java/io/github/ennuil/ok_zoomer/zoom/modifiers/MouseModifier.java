package io.github.ennuil.ok_zoomer.zoom.modifiers;

/**
 * The mouse modifier is the component that handles any change of behavior of the mouse.
 */
public interface MouseModifier {
	boolean getActive();

	double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier);

	double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier);
	
	void tick(boolean active);
}

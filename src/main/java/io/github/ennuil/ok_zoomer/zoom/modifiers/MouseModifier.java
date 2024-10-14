package io.github.ennuil.ok_zoomer.zoom.modifiers;

/**
 * The mouse modifier is the sub-instance that handles any change of behavior of the mouse.
 */
public interface MouseModifier {
	/**
	 * Gets the active state of the mouse modifier.
	 *
	 * @return the mouse modifier's active state
	 */
	boolean getActive();

	/**
	 * Modifies the cursor's X delta to the value returned on this method.
	 *
	 * @param cursorSensitivity the cursor sensitivity that is applied to the cursor delta
	 * @param cursorDeltaX the X delta after the calculations
	 * @param mouseUpdateTimeDelta the delta of the mouse update time
	 * @param targetDivisor the current zoom divisor
	 * @param transitionMultiplier the transition mode's internal multiplier
	 * @return the X delta that will replace the cursor's one
	 */
	double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier);

	/**
	 * Modifies the cursor's Y delta to the value returned on this method.
	 *
	 * @param cursorSensitivity the cursor sensitivity that is applied to the cursor delta
	 * @param cursorDeltaY the Y delta after the calculations
	 * @param mouseUpdateTimeDelta the delta of the mouse update time
	 * @param targetDivisor the current zoom divisor
	 * @param transitionMultiplier the transition mode's internal multiplier
	 * @return the Y delta that will replace the cursor's Y delta
	 */
	double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier);

	/**
	 * The tick method. Used in order to keep the internal variables accurate.
	 *
	 * @param active the zoom state
	 */
	void tick(boolean active);
}

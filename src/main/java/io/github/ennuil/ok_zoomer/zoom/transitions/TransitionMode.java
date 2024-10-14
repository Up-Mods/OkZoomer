package io.github.ennuil.ok_zoomer.zoom.transitions;

/**
 * The transition mode is a sub-instance that handles zooming itself.
 * It handles how the regular FOV will transition to the zoomed FOV and vice-versa.
 */
public interface TransitionMode {
	/**
	 * Gets the active state of the transition mode.
	 *
	 * @return the transition mode's active state
	 */
	boolean getActive();

	/**
	 * Applies the zoom to the FOV.
	 *
	 * @param fov the original FOV
	 * @param tickDelta the current tick delta
	 * @return the zoomed FOV
	 */
	double applyZoom(double fov, float tickDelta);

	/**
	 * The tick method. Used in order to keep the internal variables accurate.
	 *
	 * @param active the zoom state
	 * @param divisor the zoom divisor
	 */
	void tick(boolean active, double divisor);

	/**
	 * Gets the internal multiplier. Used for purposes other than zooming the FOV.
	 *
	 * @return the internal multiplier
	 */
	double getInternalMultiplier();
}

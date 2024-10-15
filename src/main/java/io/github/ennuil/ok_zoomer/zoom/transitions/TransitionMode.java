package io.github.ennuil.ok_zoomer.zoom.transitions;

/**
 * The transition mode is the component that handles zooming itself.
 * It handles how the regular FOV will transition to the zoomed FOV and vice-versa.
 */
public interface TransitionMode {
	boolean getActive();

	double applyZoom(double fov, float tickDelta);

	double getFade(float tickDelta);

	void tick(boolean active, double divisor);

	double getInternalMultiplier();
}

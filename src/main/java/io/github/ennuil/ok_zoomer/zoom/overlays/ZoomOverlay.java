package io.github.ennuil.ok_zoomer.zoom.overlays;

import net.minecraft.client.gui.GuiGraphics;

/**
 * The zoom overlay is a sub-instance that handles the rendering of an overlay.
 */
public interface ZoomOverlay {
	/**
	 * Gets the active state of the zoom overlay.
	 *
	 * @return the zoom overlay's active state
	 */
	boolean getActive();

	/**
	 * Determines if the zoom overlay should cancel the rendering of anything rendered after that.
	 * By default, it returns {@code false}.
	 *
	 * @return the state that will be used in order to cancel the rendering or not
	 */
	default boolean cancelOverlayRendering() { return false; }

	/**
	 * Renders the overlay itself. It's injected by LibZoomer itself.
	 *
	 * @param graphics the in-game HUD's graphics
	 */
	void renderOverlay(GuiGraphics graphics);

	/**
	 * The tick method. Used in order to keep the internal variables accurate and the overlay functional.
	 *
	 * @param active the zoom state
	 * @param divisor the zoom divisor
	 * @param transitionMultiplier the transition mode's internal multiplier
	*/
	void tick(boolean active, double divisor, double transitionMultiplier);

	/**
	 * The tick method used right before the overlay is rendered.
	 * <p>
	 * This isn't required to be implemented.
	 */
	default void tickBeforeRender() {}
}

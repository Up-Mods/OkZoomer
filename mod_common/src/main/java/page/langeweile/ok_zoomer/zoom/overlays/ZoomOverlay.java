package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import page.langeweile.ok_zoomer.zoom.transitions.TransitionMode;

/**
 * The zoom overlay is the component that handles the rendering of an overlay.
 */
public interface ZoomOverlay {
	boolean getActive();

	default boolean cancelOverlayRendering() { return false; }

	void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, TransitionMode transitionMode);

	void tick(boolean active, double divisor, TransitionMode transitionMode);

	default void tickBeforeRender(DeltaTracker deltaTracker) {}
}

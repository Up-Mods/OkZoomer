package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

/**
 * The zoom overlay is the component that handles the rendering of an overlay.
 */
public interface ZoomOverlay {
	boolean getActive();

	default boolean cancelOverlayRendering() { return false; }

	void extractOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, EasedTransitionMode transitionMode);

	void tick(boolean active, double divisor, EasedTransitionMode transitionMode);

	default void tickBeforeRender(DeltaTracker deltaTracker) {}
}

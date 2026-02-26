package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.gui.GuiGraphics;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

/**
 * The zoom overlay is the component that handles the rendering of an overlay.
 */
public interface ZoomOverlay {
	boolean getActive();

	default boolean cancelOverlayRendering() { return false; }

	void renderOverlay(GuiGraphics graphics, EasedTransitionMode transitionMode);

	void tick(boolean active, double divisor, EasedTransitionMode transitionMode);

	default void tickBeforeRender() {}
}

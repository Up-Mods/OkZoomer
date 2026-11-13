package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

public interface Overlay {
	boolean getActive();

	default boolean cancelOverlayRendering() { return false; }

	void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, EasedTransitionMode transitionMode);

	void tick(boolean active, boolean transitionActive);

	default void tickBeforeRender(DeltaTracker deltaTracker) {}
}

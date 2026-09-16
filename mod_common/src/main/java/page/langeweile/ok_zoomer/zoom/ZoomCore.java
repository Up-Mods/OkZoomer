package page.langeweile.ok_zoomer.zoom;

import page.langeweile.ok_zoomer.zoom.modifiers.MouseModifier;
import page.langeweile.ok_zoomer.zoom.overlays.ZoomOverlay;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

public record ZoomCore(
	EasedTransitionMode transitionMode,
	MouseModifier mouseModifier,
	ZoomOverlay zoomOverlay
) {}

package page.langeweile.ok_zoomer.zoom;

import page.langeweile.ok_zoomer.zoom.modifiers.MouseModifier;
import page.langeweile.ok_zoomer.zoom.overlays.Overlay;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

public record ZoomCore(
	EasedTransitionMode transitionMode,
	MouseModifier mouseModifier,
	Overlay overlay
) {}

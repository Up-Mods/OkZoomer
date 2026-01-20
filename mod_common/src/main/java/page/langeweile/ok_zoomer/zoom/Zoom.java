package page.langeweile.ok_zoomer.zoom;

import page.langeweile.ok_zoomer.zoom.modifiers.MouseModifier;
import page.langeweile.ok_zoomer.zoom.modifiers.ZoomDivisorMouseModifier;
import page.langeweile.ok_zoomer.zoom.overlays.ZoomOverlay;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;
import page.langeweile.ok_zoomer.zoom.transitions.TransitionMode;

public class Zoom {
	private static boolean zooming = false;
	private static double zoomDivisor = 4.0F;
	private static TransitionMode transitionMode = new EasedTransitionMode(
		x -> 1.0F,
		x -> 1.0F,
		x -> 1.0F,
		0,
		0,
		0,
		false,
		false
	);
	private static MouseModifier mouseModifier = new ZoomDivisorMouseModifier();
	private static ZoomOverlay zoomOverlay = null;

	public static double getZoomDivisor() {
		return Zoom.zoomDivisor;
	}

	public static void setZoomDivisor(double zoomDivisor) {
		Zoom.zoomDivisor = zoomDivisor;
	}

	public static boolean isZooming() {
		return Zoom.zooming;
	}

	public static void setZooming(boolean zooming) {
		Zoom.zooming = zooming;
	}

	public static TransitionMode getTransitionMode() {
		return transitionMode;
	}

	public static void setTransitionMode(TransitionMode transitionMode) {
		Zoom.transitionMode = transitionMode;
	}

	public static MouseModifier getMouseModifier() {
		return Zoom.mouseModifier;
	}

	public static void setMouseModifier(MouseModifier mouseModifier) {
		Zoom.mouseModifier = mouseModifier;
	}

	public static ZoomOverlay getZoomOverlay() {
		return zoomOverlay;
	}

	public static void setZoomOverlay(ZoomOverlay zoomOverlay) {
		Zoom.zoomOverlay = zoomOverlay;
	}

	public static boolean isTransitionActive() {
		return Zoom.transitionMode.getActive();
	}

	public static boolean isModifierActive() {
		return Zoom.mouseModifier != null && Zoom.mouseModifier.getActive();
	}

	public static boolean isOverlayActive() {
		return Zoom.zoomOverlay != null && Zoom.zoomOverlay.getActive();
	}
}

package io.github.ennuil.ok_zoomer.zoom;

import io.github.ennuil.ok_zoomer.zoom.modifiers.MouseModifier;
import io.github.ennuil.ok_zoomer.zoom.transitions.TransitionMode;
import io.github.ennuil.ok_zoomer.zoom.overlays.ZoomOverlay;
import io.github.ennuil.ok_zoomer.zoom.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.ok_zoomer.zoom.transitions.SmoothTransitionMode;

public class Zoom {
	private static boolean zooming = false;
	private static double defaultZoomDivisor = 4.0F;
	private static double zoomDivisor = 4.0F;
	private static TransitionMode transitionMode = new SmoothTransitionMode();
	private static MouseModifier mouseModifier = new ZoomDivisorMouseModifier();
	private static ZoomOverlay zoomOverlay = null;

	public static double getZoomDivisor() {
		return Zoom.zoomDivisor;
	}

	public static void setZoomDivisor(double zoomDivisor) {
		Zoom.zoomDivisor = zoomDivisor;
	}

	public static double getDefaultZoomDivisor() {
		return Zoom.defaultZoomDivisor;
	}

	public static void setDefaultZoomDivisor(double defaultZoomDivisor) {
		Zoom.defaultZoomDivisor = defaultZoomDivisor;
	}

	public static boolean isZooming() {
		return Zoom.zooming;
	}

	public static void setZooming(boolean zooming) {
		Zoom.zooming = zooming;
	}

	public static void resetZoomDivisor() {
		Zoom.zoomDivisor = Zoom.defaultZoomDivisor;
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

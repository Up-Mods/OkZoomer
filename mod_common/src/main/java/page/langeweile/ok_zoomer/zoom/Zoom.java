package page.langeweile.ok_zoomer.zoom;

public class Zoom {
	private static boolean zooming = false;
	private static float zoomDivisor = 4.0F;
	private static ZoomCore zoomCore = null;

	public static ZoomCore getZoomCore() {
		return Zoom.zoomCore;
	}

	public static void setZoomCore(ZoomCore zoomCore) {
		Zoom.zoomCore = zoomCore;
	}

	public static float getZoomDivisor() {
		return Zoom.zoomDivisor;
	}

	public static void setZoomDivisor(float zoomDivisor) {
		Zoom.zoomDivisor = zoomDivisor;
	}

	public static boolean isZooming() {
		return Zoom.zooming;
	}

	public static void setZooming(boolean zooming) {
		Zoom.zooming = zooming;
	}
}

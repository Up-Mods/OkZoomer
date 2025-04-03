package io.github.ennuil.ok_zoomer.zoom.modifiers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;

/**
 * An implemenation of Vanilla's Cinematic Camera as a mouse modifier.
 */
public class CinematicCameraMouseModifier implements MouseModifier {
	private boolean active;
	private Minecraft minecraft;
	private boolean cinematicCameraEnabled;
	private final SmoothDouble cursorXZoomSmoother = new SmoothDouble();
	private final SmoothDouble cursorYZoomSmoother = new SmoothDouble();

	/**
	 * Initializes an instance of the cinematic camera mouse modifier.
	*/
	public CinematicCameraMouseModifier() {
		this.active = false;
		this.ensureClient();
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		if (this.cinematicCameraEnabled) {
			this.cursorXZoomSmoother.reset();
			return cursorDeltaX;
		}

		return this.cursorXZoomSmoother.getNewDeltaValue(cursorDeltaX, mouseUpdateTimeDelta * cursorSensitivity);
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
		if (this.cinematicCameraEnabled) {
			this.cursorYZoomSmoother.reset();
			return cursorDeltaY;
		}

		return this.cursorYZoomSmoother.getNewDeltaValue(cursorDeltaY, mouseUpdateTimeDelta * cursorSensitivity);
	}

	@Override
	public void tick(boolean active) {
		this.ensureClient();
		this.cinematicCameraEnabled = this.minecraft.options.smoothCamera;
		if (!active && this.active) {
			this.cursorXZoomSmoother.reset();
			this.cursorYZoomSmoother.reset();
		}
		this.active = active;
	}

	private void ensureClient() {
		if (this.minecraft == null) {
			this.minecraft = Minecraft.getInstance();
		}
	}
}

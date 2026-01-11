package page.langeweile.ok_zoomer.zoom.modifiers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;

/**
 * An implemenation of Vanilla's Cinematic Camera as a mouse modifier.
 */
public class CinematicCameraMouseModifier implements MouseModifier {
	private final SmoothDouble cursorXZoomSmoother = new SmoothDouble();
	private final SmoothDouble cursorYZoomSmoother = new SmoothDouble();
	private final float multiplier;
	private boolean active;
	private Minecraft minecraft;
	private boolean cinematicCameraEnabled;

	/**
	 * Initializes an instance of the cinematic camera mouse modifier.
	*/
	public CinematicCameraMouseModifier(float multiplier) {
		this.multiplier = multiplier;
		this.active = false;
		this.ensureClient();
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		if (this.cinematicCameraEnabled) {
			this.cursorXZoomSmoother.reset();
			return cursorDeltaX;
		}

		return this.cursorXZoomSmoother.getNewDeltaValue(cursorDeltaX, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		if (this.cinematicCameraEnabled) {
			this.cursorYZoomSmoother.reset();
			return cursorDeltaY;
		}

		return this.cursorYZoomSmoother.getNewDeltaValue(cursorDeltaY, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
	}

	@Override
	public void tick(boolean active) {
		this.ensureClient();

		if (this.multiplier == 1.0F) {
			if (this.minecraft.options.smoothCamera && !this.cinematicCameraEnabled) {
				this.cursorXZoomSmoother.reset();
				this.cursorYZoomSmoother.reset();
			}
		}

		if (!active && this.active) {
			this.cursorXZoomSmoother.reset();
			this.cursorYZoomSmoother.reset();
		}

		this.cinematicCameraEnabled = this.minecraft.options.smoothCamera;
		this.active = active;
	}

	private void ensureClient() {
		if (this.minecraft == null) {
			this.minecraft = Minecraft.getInstance();
		}
	}
}

package page.langeweile.ok_zoomer.zoom.modifiers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;

public class CinematicCameraMouseModifier implements MouseModifier {
	private final SmoothDouble cursorXZoomSmoother = new SmoothDouble();
	private final SmoothDouble cursorYZoomSmoother = new SmoothDouble();
	private final float multiplier;
	private boolean active;

	public CinematicCameraMouseModifier(float multiplier) {
		this.multiplier = multiplier;
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		return this.cursorXZoomSmoother.getNewDeltaValue(cursorDeltaX, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
	}

	@Override
	public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double transitionMultiplier) {
		return this.cursorYZoomSmoother.getNewDeltaValue(cursorDeltaY, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
	}

	@Override
	public void tick(boolean active, boolean transitionActive) {
		if (!active && this.active) {
			this.cursorXZoomSmoother.reset();
			this.cursorYZoomSmoother.reset();
		}

		this.active = active;
	}
}

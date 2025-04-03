package io.github.ennuil.ok_zoomer.zoom.transitions;

import net.minecraft.util.Mth;

// The implementation of the linear transition
public class LinearTransitionMode implements TransitionMode {
	private boolean active;
	private final double linearStep;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;

	public LinearTransitionMode(double linearStep) {
		this.active = false;
		this.linearStep = linearStep;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
	}

	@Override
	public boolean getActive() {
		return this.active || this.internalMultiplier != 1.0F || this.internalFade != 0.0F;
	}

	@Override
	public float applyZoom(float fov, float tickDelta) {
		return fov * Mth.lerp(tickDelta, this.lastInternalMultiplier, this.internalMultiplier);
	}

	@Override
	public float getFade(float tickDelta) {
		return Mth.lerp(tickDelta, this.lastInternalFade, this.internalFade);
	}

	@Override
	public void tick(boolean active, double divisor) {
		float zoomMultiplier = (float) (1.0 / divisor);
		float fadeMultiplier = active ? 1.0F : 0.0F;

		this.lastInternalMultiplier = this.internalMultiplier;
		this.lastInternalFade = this.internalFade;

		this.internalMultiplier = Mth.approach(this.internalMultiplier, zoomMultiplier, (float) this.linearStep);
		this.internalFade = Mth.approach(this.internalFade, fadeMultiplier, (float) this.linearStep);

		this.active = active;
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

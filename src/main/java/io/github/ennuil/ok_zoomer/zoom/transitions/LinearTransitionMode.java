package io.github.ennuil.ok_zoomer.zoom.transitions;

import net.minecraft.util.Mth;

// The implementation of the linear transition
public class LinearTransitionMode implements TransitionMode {
	private boolean active;
	private final double minimumLinearStep;
	private final double maximumLinearStep;
	private float fovMultiplier;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;

	public LinearTransitionMode(double minimumLinearStep, double maximumLinearStep) {
		this.active = false;
		this.minimumLinearStep = minimumLinearStep;
		this.maximumLinearStep = maximumLinearStep;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public float applyZoom(float fov, float tickDelta) {
		fovMultiplier = Mth.lerp(tickDelta, this.lastInternalMultiplier, this.internalMultiplier);
		return fov * fovMultiplier;
	}

	@Override
	public float getFade(float tickDelta) {
		return Mth.lerp(tickDelta, this.lastInternalFade, this.internalFade);
	}

	@Override
	public void tick(boolean active, double divisor) {
		double zoomMultiplier = 1.0 / divisor;
		double fadeMultiplier = active ? 1.0D : 0.0D;

		this.lastInternalMultiplier = this.internalMultiplier;
		this.lastInternalFade = this.internalFade;

		double linearStep = Mth.clamp(zoomMultiplier, this.minimumLinearStep, this.maximumLinearStep);
		this.internalMultiplier = Mth.approach(this.internalMultiplier, (float) zoomMultiplier, (float) linearStep);
		this.internalFade = Mth.approach(this.internalFade, (float) fadeMultiplier, (float) linearStep);

		if (active || fovMultiplier == this.internalMultiplier) {
			this.active = active;
		}
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

package io.github.ennuil.ok_zoomer.zoom.transitions;

import net.minecraft.util.Mth;

public class SmoothTransitionMode implements TransitionMode {
	private boolean active;
	private final float smoothMultiplier;
	private double fovMultiplier;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;

	public SmoothTransitionMode(float smoothMultiplier) {
		this.active = false;
		this.smoothMultiplier = smoothMultiplier;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
	}

	public SmoothTransitionMode() {
		this(0.5F);
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public double applyZoom(double fov, float tickDelta) {
		fovMultiplier = Mth.lerp(tickDelta, this.lastInternalMultiplier, this.internalMultiplier);
		return fov * fovMultiplier;
	}

	@Override
	public double getFade(float tickDelta) {
		return Mth.lerp(tickDelta, this.lastInternalFade, this.internalFade);
	}

	@Override
	public void tick(boolean active, double divisor) {
		double zoomMultiplier = 1.0 / divisor;
		double fadeMultiplier = active ? 1.0 : 0.0;

		this.lastInternalMultiplier = this.internalMultiplier;
		this.lastInternalFade = this.internalFade;

		this.internalMultiplier += (float) ((zoomMultiplier - this.internalMultiplier) * this.smoothMultiplier);
		this.internalFade += (float) ((fadeMultiplier - this.internalFade) * this.smoothMultiplier);

		if (active || fovMultiplier == this.internalMultiplier) {
			this.active = active;
		}
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

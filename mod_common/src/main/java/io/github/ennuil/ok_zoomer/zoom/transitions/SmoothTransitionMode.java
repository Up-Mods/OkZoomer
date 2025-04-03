package io.github.ennuil.ok_zoomer.zoom.transitions;

import net.minecraft.util.Mth;

public class SmoothTransitionMode implements TransitionMode {
	private boolean active;
	private final float smoothMultiplier;
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

		this.internalMultiplier += (zoomMultiplier - this.internalMultiplier) * this.smoothMultiplier;
		this.internalFade += (fadeMultiplier - this.internalFade) * this.smoothMultiplier;

		this.active = active;
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

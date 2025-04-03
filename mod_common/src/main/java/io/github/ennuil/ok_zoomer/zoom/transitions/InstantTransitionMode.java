package io.github.ennuil.ok_zoomer.zoom.transitions;

public class InstantTransitionMode implements TransitionMode {
	private boolean active;
	private double divisor;

	public InstantTransitionMode() {
		this.active = false;
		this.divisor = 1.0;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public float applyZoom(float fov, float tickDelta) {
		return fov / (float) this.divisor;
	}

	@Override
	public float getFade(float tickDelta) {
		return this.active ? 1.0F : 0.0F;
	}

	@Override
	public void tick(boolean active, double divisor) {
		this.active = active;
		this.divisor = divisor;
	}

	@Override
	public double getInternalMultiplier() {
		return 1.0 / this.divisor;
	}
}

package page.langeweile.ok_zoomer.zoom.transitions;

import net.minecraft.util.Mth;
import page.langeweile.ok_zoomer.config.OkZoomerConfigManager;

public class EasedTransitionMode implements TransitionMode {
	private boolean active;
	private int ticks;
	private int targetTicks;
	private float startZoomMultiplier;
	private float startFadeMultiplier;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;

	public EasedTransitionMode() {
		this.active = false;
		this.ticks = 0;
		this.startZoomMultiplier = 1.0F;
		this.startFadeMultiplier = 0.0F;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
	}

	@Override
	public boolean getActive() {
		return this.active || this.ticks <= this.targetTicks;
	}

	@Override
	public float applyZoom(float fov, float tickDelta) {
		return fov * Mth.lerp(tickDelta, this.lastInternalMultiplier, this.internalMultiplier);
	}

	@Override
	public float getFade(float tickDelta) {
		return Mth.lerp(tickDelta, this.lastInternalFade, this.internalFade);
	}

	// Once logic is finished? Throw this mess into a profiler
	@Override
	public void tick(boolean active, double divisor) {
		float zoomMultiplier = (float) (1.0 / divisor);
		float fadeMultiplier = active ? 1.0F : 0.0F;

		this.targetTicks = active
			? OkZoomerConfigManager.CONFIG.transitionValues.easeInTicks.value()
			: OkZoomerConfigManager.CONFIG.transitionValues.easeOutTicks.value();

		float oppositeTargetTicks = active
			? OkZoomerConfigManager.CONFIG.transitionValues.easeOutTicks.value()
			: OkZoomerConfigManager.CONFIG.transitionValues.easeInTicks.value();

		if (this.ticks <= this.targetTicks) {
			this.ticks += 1;
		}

		if (this.active != active) {
			if (active && this.ticks >= oppositeTargetTicks) {
				this.internalMultiplier = 1.0F;
				this.internalFade = 0.0F;
			}
			this.ticks = 0;
			this.startZoomMultiplier = this.internalMultiplier;
			this.startFadeMultiplier = this.internalFade;
		}

		this.lastInternalMultiplier = this.internalMultiplier;
		this.lastInternalFade = this.internalFade;

		float rawProgress = Math.min(this.ticks, this.targetTicks) / (float) this.targetTicks;
		float progress = (float) (1.0 - Math.pow(2.0, -10.0 * rawProgress));

		System.out.println("progress :" + progress + " - ticks: " + ticks + " - start: " + this.startZoomMultiplier);

		if (this.ticks <= this.targetTicks) {
			this.internalMultiplier = Mth.lerp(progress, this.startZoomMultiplier, active ? zoomMultiplier : 1.0F);
			this.internalFade = Mth.lerp(progress, this.startFadeMultiplier, active ? fadeMultiplier : 0.0F);
		} else {
			this.internalMultiplier += (zoomMultiplier - this.internalMultiplier) * 0.6F;
			this.internalFade += (fadeMultiplier - this.internalFade) * 0.6F;
		}

		this.active = active;
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

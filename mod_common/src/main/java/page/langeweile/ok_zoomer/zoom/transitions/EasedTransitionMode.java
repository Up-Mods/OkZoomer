package page.langeweile.ok_zoomer.zoom.transitions;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.Mth;

public class EasedTransitionMode implements TransitionMode {
	private final FloatUnaryOperator inwardTransitionMode;
	private final FloatUnaryOperator outwardTransitionMode;
	private final int targetInwardTicks;
	private final int targetOutwardTicks;

	private boolean active;
	private int ticks;
	private float startZoomMultiplier;
	private float startFadeMultiplier;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;

	public EasedTransitionMode(
		FloatUnaryOperator inwardTransitionMode,
		FloatUnaryOperator outwardTransitionMode,
		int targetInwardTicks,
		int targetOutwardTicks
	) {
		this.inwardTransitionMode = inwardTransitionMode;
		this.outwardTransitionMode = outwardTransitionMode;
		this.targetInwardTicks = targetInwardTicks;
		this.targetOutwardTicks = targetOutwardTicks;
		this.active = false;
		this.ticks = 1;
		this.startZoomMultiplier = 1.0F;
		this.startFadeMultiplier = 0.0F;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
	}

	@Override
	public boolean getActive() {
		return this.active || this.ticks <= this.targetOutwardTicks;
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

		boolean skipTransition = active && this.targetInwardTicks == 0 || !active && this.targetOutwardTicks == 0;

		if (skipTransition) {
			this.internalMultiplier = active ? zoomMultiplier : 1.0F;
			this.internalFade = active ? fadeMultiplier : 1.0F;
			this.lastInternalMultiplier = this.internalMultiplier;
			this.lastInternalFade = this.internalFade;
		} else {
			int targetTicks = active ? targetInwardTicks : targetOutwardTicks;
			int oppositeTargetTicks = active ? targetOutwardTicks : targetInwardTicks;

			if (this.ticks <= targetTicks) {
				this.ticks += 1;
			}

			this.lastInternalMultiplier = this.internalMultiplier;
			this.lastInternalFade = this.internalFade;

			if (this.active != active) {
				if (active && this.ticks >= oppositeTargetTicks) {
					this.internalMultiplier = 1.0F;
					this.internalFade = 0.0F;
				}
				this.ticks = 1;
				this.startZoomMultiplier = this.internalMultiplier;
				this.startFadeMultiplier = this.internalFade;
			}

			float rawProgress = Math.min(this.ticks, targetTicks) / (float) targetTicks;
			float progress = this.ticks <= targetTicks
				? (active ? inwardTransitionMode.apply(rawProgress) : outwardTransitionMode.apply(rawProgress))
				: 1.0F;
			progress = Math.min(progress, 1.2F);

			System.out.println("progress :" + progress + " - ticks: " + ticks + " - start: " + this.startZoomMultiplier);

			if (this.ticks <= targetTicks) {
				this.internalMultiplier = Mth.lerp(progress, this.startZoomMultiplier, active ? zoomMultiplier : 1.0F);
				this.internalFade = Mth.lerp(progress, this.startFadeMultiplier, active ? fadeMultiplier : 0.0F);
			} else {
				// TODO - Move to tick-based logic
				this.internalMultiplier += (zoomMultiplier - this.internalMultiplier) * 0.6F;
				this.internalFade += (fadeMultiplier - this.internalFade) * 0.6F;
			}
		}

		this.active = active;
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

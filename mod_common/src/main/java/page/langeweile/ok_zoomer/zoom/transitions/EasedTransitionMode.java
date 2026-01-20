package page.langeweile.ok_zoomer.zoom.transitions;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.util.Mth;

public class EasedTransitionMode implements TransitionMode {
	private final FloatUnaryOperator startTransition;
	private final FloatUnaryOperator endTransition;
	private final FloatUnaryOperator scrollTransition;
	private final int targetStartTicks;
	private final int targetEndTicks;
	private final int targetScrollTicks;
	private final boolean invertStartTransition;
	private final boolean invertEndTransition;

	private boolean active;
	private int ticks;
	private float startZoomMultiplier;
	private float startFadeMultiplier;
	private float lastZoomMultiplier;
	private float internalMultiplier;
	private float lastInternalMultiplier;
	private float internalFade;
	private float lastInternalFade;
	private boolean scrollMode;

	public EasedTransitionMode(
		FloatUnaryOperator startTransition,
		FloatUnaryOperator endTransition,
		FloatUnaryOperator scrollTransition,
		int targetStartTicks,
		int targetEndTicks,
		int targetScrollTicks,
		boolean invertStartTransition,
		boolean invertEndTransition
	) {
		this.startTransition = startTransition;
		this.endTransition = endTransition;
		this.scrollTransition = scrollTransition;
		this.targetStartTicks = targetStartTicks;
		this.targetEndTicks = targetEndTicks;
		this.targetScrollTicks = targetScrollTicks;
		this.invertStartTransition = invertStartTransition;
		this.invertEndTransition = invertEndTransition;

		this.active = false;
		this.ticks = 1;
		this.startZoomMultiplier = 1.0F;
		this.startFadeMultiplier = 0.0F;
		this.lastZoomMultiplier = 1.0F;
		this.internalMultiplier = 1.0F;
		this.lastInternalMultiplier = 1.0F;
		this.internalFade = 0.0F;
		this.lastInternalFade = 0.0F;
		this.scrollMode = false;
	}

	@Override
	public boolean getActive() {
		return this.active || this.ticks <= this.targetEndTicks;
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

		boolean skipTransition = active && this.targetStartTicks == 0 || !active && this.targetEndTicks == 0;

		if (skipTransition) {
			this.internalMultiplier = active ? zoomMultiplier : 1.0F;
			this.internalFade = active ? fadeMultiplier : 1.0F;
			this.lastInternalMultiplier = this.internalMultiplier;
			this.lastInternalFade = this.internalFade;
		} else {
			int targetTicks = active ? (this.scrollMode ? this.targetScrollTicks : this.targetStartTicks) : this.targetEndTicks;
			int oppositeTargetTicks = active ? this.targetEndTicks : this.targetStartTicks;
			// After a quick test, I decided that inverting the scroll transition would be more detrimental than inverting a regular transition.
			// If you personally would be benefitted by an "Invert Scroll Transition" option, let me know on GitHub!
			boolean invert = active ? (!this.scrollMode && this.invertStartTransition) : this.invertEndTransition;

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
				this.scrollMode = false;
			}

			if (active && this.ticks != 1 && zoomMultiplier != this.lastZoomMultiplier) {
				this.scrollMode = true;
				this.ticks = this.targetScrollTicks / 2;
				this.lastZoomMultiplier = this.internalMultiplier;
			}

			if (this.scrollMode) {
				if (this.targetScrollTicks == 0) {
					this.internalMultiplier = active ? zoomMultiplier : 1.0F;
					this.lastInternalMultiplier = this.internalMultiplier;
				}

				if (zoomMultiplier != this.lastZoomMultiplier) {
					this.ticks = 1;
					this.startZoomMultiplier = this.lastZoomMultiplier;
					this.startFadeMultiplier = fadeMultiplier;
				}
			}

			float rawProgress = Math.min(this.ticks, targetTicks) / (float) targetTicks;
			if (invert) {
				rawProgress = 1.0F - rawProgress;
			}
			float progress = this.ticks <= targetTicks
				? (active
					? (this.scrollMode ? this.scrollTransition.apply(rawProgress) : this.startTransition.apply(rawProgress))
					: this.endTransition.apply(rawProgress))
				: 1.0F;
			progress = Math.min(invert ? 1.0F - progress : progress, 1.2F);

			if (this.ticks <= targetTicks) {
				this.internalMultiplier = Mth.lerp(progress, this.startZoomMultiplier, active ? zoomMultiplier : 1.0F);
				this.internalFade = Mth.lerp(progress, this.startFadeMultiplier, active ? fadeMultiplier : 0.0F);
			}
		}

		this.active = active;
		this.lastZoomMultiplier = zoomMultiplier;
	}

	@Override
	public double getInternalMultiplier() {
		return this.internalMultiplier;
	}
}

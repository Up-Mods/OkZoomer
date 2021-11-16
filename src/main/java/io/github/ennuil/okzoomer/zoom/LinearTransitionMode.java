package io.github.ennuil.okzoomer.zoom;

import io.github.ennuil.libzoomer.api.TransitionMode;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

// The implementation of the linear transition
public class LinearTransitionMode implements TransitionMode {
    private static final Identifier TRANSITION_ID = new Identifier("okzoomer:linear_transition");
    private boolean active;
    private double minimumLinearStep;
    private double maximumLinearStep;
    private double linearStep;
    private double fovMultiplier;
    private float internalMultiplier;
    private float lastInternalMultiplier;

    public LinearTransitionMode(double minimumLinearStep, double maximumLinearStep) {
        this.active = false;
        this.minimumLinearStep = minimumLinearStep;
        this.maximumLinearStep = maximumLinearStep;
        this.internalMultiplier = 1.0F;
        this.lastInternalMultiplier = 1.0F;
    }

    @Override
    public Identifier getIdentifier() {
        return TRANSITION_ID;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public double applyZoom(double fov, float tickDelta) {
        fovMultiplier = MathHelper.lerp(tickDelta, this.lastInternalMultiplier, this.internalMultiplier);
        return fov * fovMultiplier;
    }

    @Override
    public void tick(boolean active, double divisor) {
        double zoomMultiplier = 1.0D / divisor;

        this.lastInternalMultiplier = this.internalMultiplier;
        
        this.linearStep = MathHelper.clamp(zoomMultiplier, this.minimumLinearStep, this.maximumLinearStep);
        this.internalMultiplier = MathHelper.stepTowards((float)this.internalMultiplier, (float)zoomMultiplier, (float)linearStep);

        if ((!active && fovMultiplier == this.internalMultiplier) || active) {
            this.active = active;
        }
    }

    @Override
    public double getInternalMultiplier() {
        return this.internalMultiplier;
    }
}

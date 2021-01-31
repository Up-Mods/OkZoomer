package io.github.joaoh1.okzoomer.zoom;

import io.github.joaoh1.libzoomer.api.MouseModifier;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.Identifier;

public class MultipliedCinematicCameraMouseModifier implements MouseModifier {
    private Identifier transitionId = new Identifier("libzoomer:cinematic_camera");
    private boolean active;
    private boolean cinematicCameraEnabled;
    private final SmoothUtil cursorXZoomSmoother = new SmoothUtil();
    private final SmoothUtil cursorYZoomSmoother = new SmoothUtil();
    private double cinematicCameraMultiplier;

    public MultipliedCinematicCameraMouseModifier(double cinematicCameraMultiplier) {
        this.active = false;
        this.cinematicCameraMultiplier = cinematicCameraMultiplier;
    }
    
    @Override
    public Identifier getIdentifier() {
        return this.transitionId;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public double applyXModifier(double cursorXDelta, double o, double mouseUpdateDelta, double targetDivisor, double transitionMultiplier) {
        if (this.cinematicCameraEnabled) {
            this.cursorXZoomSmoother.clear();
            return o;
        }
        double multiplier = mouseUpdateDelta * cinematicCameraMultiplier;
        if (cursorXDelta != 0) {
            multiplier *= (o / cursorXDelta);
        }
        return this.cursorXZoomSmoother.smooth(o, multiplier);
    }

    @Override
    public double applyYModifier(double cursorYDelta, double p, double mouseUpdateDelta, double targetDivisor, double transitionMultiplier) {
        if (this.cinematicCameraEnabled) {
            this.cursorYZoomSmoother.clear();
            return p;
        }
        double multiplier = mouseUpdateDelta * cinematicCameraMultiplier;
        if (cursorYDelta != 0) {
            multiplier *= (p / cursorYDelta);
        }
        return this.cursorYZoomSmoother.smooth(p, multiplier);
    }

    @Override
    public void tick(boolean active, boolean cinematicCameraEnabled) {
        this.cinematicCameraEnabled = cinematicCameraEnabled;
        this.active = active;
        if (this.active == false) {
            this.cursorXZoomSmoother.clear();
            this.cursorYZoomSmoother.clear();
        }
    }
}
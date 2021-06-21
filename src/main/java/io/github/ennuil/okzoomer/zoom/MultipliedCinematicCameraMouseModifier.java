package io.github.ennuil.okzoomer.zoom;

import io.github.ennuil.libzoomer.api.MouseModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.Identifier;

// The implementation of the multiplied cinematic camera
public class MultipliedCinematicCameraMouseModifier implements MouseModifier {
    private Identifier transitionId = new Identifier("okzoomer:multiplied_cinematic_camera");
    private boolean active;
    private MinecraftClient client;
    private boolean cinematicCameraEnabled;
    private final SmoothUtil cursorXZoomSmoother = new SmoothUtil();
    private final SmoothUtil cursorYZoomSmoother = new SmoothUtil();
    private double cinematicCameraMultiplier;

    public MultipliedCinematicCameraMouseModifier(double cinematicCameraMultiplier) {
        super();
        this.cinematicCameraMultiplier = cinematicCameraMultiplier;
        this.client = MinecraftClient.getInstance();
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
    public double applyXModifier(double cursorDeltaX, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
        if (this.cinematicCameraEnabled) {
            this.cursorXZoomSmoother.clear();
            return cursorDeltaX;
        }
        double smoother = mouseUpdateTimeDelta * cinematicCameraMultiplier * cursorSensitivity;
        return this.cursorXZoomSmoother.smooth(cursorDeltaX, smoother);
    }

    @Override
    public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
        if (this.cinematicCameraEnabled) {
            this.cursorYZoomSmoother.clear();
            return cursorDeltaY;
        }
        double smoother = mouseUpdateTimeDelta * cinematicCameraMultiplier * cursorSensitivity;
        return this.cursorYZoomSmoother.smooth(cursorDeltaY, smoother);
    }

    @Override
    public void tick(boolean active) {
        this.cinematicCameraEnabled = this.client.options.smoothCameraEnabled;
        this.active = active;
        if (!this.active) {
            this.cursorXZoomSmoother.clear();
            this.cursorYZoomSmoother.clear();
        }
    }
}
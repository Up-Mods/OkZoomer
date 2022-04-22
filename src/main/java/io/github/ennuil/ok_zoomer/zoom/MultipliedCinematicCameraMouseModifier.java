package io.github.ennuil.ok_zoomer.zoom;

import io.github.ennuil.libzoomer.api.MouseModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.Identifier;

// The implementation of the multiplied cinematic camera
public class MultipliedCinematicCameraMouseModifier implements MouseModifier {
    private static final Identifier MODIFIER_ID = new Identifier("ok_zoomer:multiplied_cinematic_camera");
    private boolean active;
    private final MinecraftClient client;
    private boolean cinematicCameraEnabled;
    private final SmoothUtil cursorXZoomSmoother = new SmoothUtil();
    private final SmoothUtil cursorYZoomSmoother = new SmoothUtil();
    private double cinematicCameraMultiplier;

    public MultipliedCinematicCameraMouseModifier(double cinematicCameraMultiplier) {
        this.cinematicCameraMultiplier = cinematicCameraMultiplier;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public Identifier getIdentifier() {
        return MODIFIER_ID;
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
        this.cinematicCameraEnabled = this.client.options.cinematicCamera;
        if (!active && active != this.active) {
            this.cursorXZoomSmoother.clear();
            this.cursorYZoomSmoother.clear();
        }
        this.active = active;
    }
}

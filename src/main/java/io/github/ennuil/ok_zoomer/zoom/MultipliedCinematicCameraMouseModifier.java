package io.github.ennuil.ok_zoomer.zoom;

import io.github.ennuil.libzoomer.api.MouseModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.Identifier;

// The implementation of the multiplied cinematic camera
public class MultipliedCinematicCameraMouseModifier implements MouseModifier {
    private static final Identifier MODIFIER_ID = new Identifier("ok_zoomer:multiplied_cinematic_camera");
    private final MinecraftClient client;
    private final SmoothUtil cursorXZoomSmoother = new SmoothUtil();
    private final SmoothUtil cursorYZoomSmoother = new SmoothUtil();
    private boolean active;
    private boolean cinematicCameraEnabled;
    private final double multiplier;

    public MultipliedCinematicCameraMouseModifier(double multiplier) {
        this.multiplier = multiplier;
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

        return this.cursorXZoomSmoother.smooth(cursorDeltaX, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
    }

    @Override
    public double applyYModifier(double cursorDeltaY, double cursorSensitivity, double mouseUpdateTimeDelta, double targetDivisor, double transitionMultiplier) {
        if (this.cinematicCameraEnabled) {
            this.cursorYZoomSmoother.clear();
            return cursorDeltaY;
        }

        return this.cursorYZoomSmoother.smooth(cursorDeltaY, mouseUpdateTimeDelta * this.multiplier * cursorSensitivity);
    }

    @Override
    public void tick(boolean active) {
        this.cinematicCameraEnabled = this.client.options.cinematicCamera;
        if (!active && this.active) {
            this.cursorXZoomSmoother.clear();
            this.cursorYZoomSmoother.clear();
        }
        this.active = active;
    }
}

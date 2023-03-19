package io.github.ennuil.ok_zoomer.zoom;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

// Implements the zoom overlay
public class ZoomerZoomOverlay implements ZoomOverlay {
    private static final Identifier OVERLAY_ID = new Identifier("ok_zoomer:zoom_overlay");
    private final Identifier textureId;
    private boolean active;
    private final MinecraftClient client;

    public float zoomOverlayAlpha = 0.0F;
    public float lastZoomOverlayAlpha = 0.0F;

    public ZoomerZoomOverlay(Identifier textureId) {
        this.textureId = textureId;
        this.active = false;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public Identifier getIdentifier() {
        return OVERLAY_ID;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public void renderOverlay(MatrixStack matrices) {
		int scaledWidth = this.client.getWindow().getScaledWidth();
		int scaledHeight = this.client.getWindow().getScaledHeight();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.zoomOverlayAlpha);
		RenderSystem.setShaderTexture(0, this.textureId);
		DrawableHelper.drawTexture(matrices, 0, 0, -90, 0.0F, 0.0F, scaledWidth, scaledHeight, scaledWidth, scaledHeight);
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void tick(boolean active, double divisor, double transitionMultiplier) {
        if (active || zoomOverlayAlpha == 0.0f) {
            this.active = active;
        }

        /*
        Due to how LibZoomer is implemented, it's always going to disappear when the HUD's hidden,
        this is not good for cinematic purposes...
        // TODO - Restore this feature
        if (this.client.options.hudHidden) {
            if (OkZoomerConfigPojo.tweaks.hideZoomOverlay) {
                return;
            }
        }
        */

        float zoomMultiplier = active ? 1.0F : 0.0F;

        lastZoomOverlayAlpha = zoomOverlayAlpha;

        if (OkZoomerConfigManager.ZOOM_TRANSITION.value().equals(ZoomTransitionOptions.SMOOTH)) {
            zoomOverlayAlpha += (zoomMultiplier - zoomOverlayAlpha) * OkZoomerConfigManager.SMOOTH_MULTIPLIER.value();
        } else if (OkZoomerConfigManager.ZOOM_TRANSITION.value().equals(ZoomTransitionOptions.LINEAR)) {
            double linearStep = MathHelper.clamp(1.0F / divisor, OkZoomerConfigManager.MINIMUM_LINEAR_STEP.value(), OkZoomerConfigManager.MAXIMUM_LINEAR_STEP.value());

            zoomOverlayAlpha = MathHelper.stepTowards(zoomOverlayAlpha, zoomMultiplier, (float)linearStep);
        }
    }
}

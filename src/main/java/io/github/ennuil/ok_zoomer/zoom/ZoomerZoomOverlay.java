package io.github.ennuil.ok_zoomer.zoom;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
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
    public void renderOverlay(GuiGraphics graphics) {
		int scaledWidth = this.client.getWindow().getScaledWidth();
		int scaledHeight = this.client.getWindow().getScaledHeight();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		float lerpedOverlayAlpha = MathHelper.lerp(this.client.getTickDelta(), this.lastZoomOverlayAlpha, this.zoomOverlayAlpha);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, lerpedOverlayAlpha);
		graphics.drawTexture(this.textureId, 0, 0, -90, 0.0F, 0.0F, scaledWidth, scaledHeight, scaledWidth, scaledHeight);
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

        if (OkZoomerConfigManager.CONFIG.features.zoomTransition.value().equals(ZoomTransitionOptions.SMOOTH)) {
            zoomOverlayAlpha += (float) ((zoomMultiplier - zoomOverlayAlpha) * OkZoomerConfigManager.CONFIG.transitionValues.smoothTransitionFactor.value());
        } else if (OkZoomerConfigManager.CONFIG.features.zoomTransition.value().equals(ZoomTransitionOptions.LINEAR)) {
            double linearStep = MathHelper.clamp(
				1.0F / divisor,
				OkZoomerConfigManager.CONFIG.transitionValues.minimumLinearStep.value(),
				OkZoomerConfigManager.CONFIG.transitionValues.maximumLinearStep.value()
			);

            zoomOverlayAlpha = MathHelper.stepTowards(zoomOverlayAlpha, zoomMultiplier, (float)linearStep);
        }
    }
}

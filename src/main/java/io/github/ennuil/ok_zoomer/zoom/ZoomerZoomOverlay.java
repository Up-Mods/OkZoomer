package io.github.ennuil.ok_zoomer.zoom;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

// Implements the zoom overlay
public class ZoomerZoomOverlay implements ZoomOverlay {
    private static final ResourceLocation OVERLAY_ID = new ResourceLocation("ok_zoomer:zoom_overlay");
    private final ResourceLocation textureId;
    private boolean active;
    private final Minecraft client;

    public float zoomOverlayAlpha = 0.0F;
    public float lastZoomOverlayAlpha = 0.0F;

    public ZoomerZoomOverlay(ResourceLocation textureId) {
        this.textureId = textureId;
        this.active = false;
        this.client = Minecraft.getInstance();
    }

    @Override
    public ResourceLocation getIdentifier() {
        return OVERLAY_ID;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics) {
		int scaledWidth = this.client.getWindow().getGuiScaledWidth();
		int scaledHeight = this.client.getWindow().getGuiScaledHeight();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		float lerpedOverlayAlpha = Mth.lerp(this.client.getFrameTime(), this.lastZoomOverlayAlpha, this.zoomOverlayAlpha);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, lerpedOverlayAlpha);
		graphics.blit(this.textureId, 0, 0, -90, 0.0F, 0.0F, scaledWidth, scaledHeight, scaledWidth, scaledHeight);
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

        if (OkZoomerConfigManager.CONFIG.features.zoom_transition.value().equals(ZoomTransitionOptions.SMOOTH)) {
            zoomOverlayAlpha += (float) ((zoomMultiplier - zoomOverlayAlpha) * OkZoomerConfigManager.CONFIG.values.smooth_multiplier.value());
        } else if (OkZoomerConfigManager.CONFIG.features.zoom_transition.value().equals(ZoomTransitionOptions.LINEAR)) {
            double linearStep = Mth.clamp(
				1.0F / divisor,
				OkZoomerConfigManager.CONFIG.values.minimum_linear_step.value(),
				OkZoomerConfigManager.CONFIG.values.maximum_linear_step.value()
			);

            zoomOverlayAlpha = Mth.approach(zoomOverlayAlpha, zoomMultiplier, (float)linearStep);
        }
    }
}

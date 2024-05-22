package io.github.ennuil.ok_zoomer.zoom;

import com.mojang.blaze3d.platform.GlStateManager;
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
    private final Minecraft minecraft;

    public float zoomOverlayAlpha = 0.0F;
    public float lastZoomOverlayAlpha = 0.0F;

    public ZoomerZoomOverlay(ResourceLocation textureId) {
        this.textureId = textureId;
        this.active = false;
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public ResourceLocation getId() {
        return OVERLAY_ID;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics) {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		float lerpedOverlayAlpha = Mth.lerp(this.minecraft.getFrameTime(), this.lastZoomOverlayAlpha, this.zoomOverlayAlpha);
		RenderSystem.setShaderColor(lerpedOverlayAlpha, lerpedOverlayAlpha, lerpedOverlayAlpha, 1.0F);
		graphics.blit(this.textureId, 0, 0, -90, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
    }

    @Override
    public void tick(boolean active, double divisor, double transitionMultiplier) {
        if (active || zoomOverlayAlpha == 0.0F) {
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
            double linearStep = Mth.clamp(
				1.0F / divisor,
				OkZoomerConfigManager.CONFIG.transitionValues.minimumLinearStep.value(),
				OkZoomerConfigManager.CONFIG.transitionValues.maximumLinearStep.value()
			);

            zoomOverlayAlpha = Mth.approach(zoomOverlayAlpha, zoomMultiplier, (float) linearStep);
        }
    }
}

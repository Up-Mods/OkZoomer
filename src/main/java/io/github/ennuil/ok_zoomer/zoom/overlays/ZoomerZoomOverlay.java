package io.github.ennuil.ok_zoomer.zoom.overlays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.config.ConfigEnums.ZoomTransitionOptions;
import io.github.ennuil.ok_zoomer.config.OkZoomerConfigManager;
import io.github.ennuil.ok_zoomer.zoom.transitions.TransitionMode;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

// Implements the zoom overlay
public class ZoomerZoomOverlay implements ZoomOverlay {
    private final ResourceLocation textureId;
    private boolean active;

    public ZoomerZoomOverlay(ResourceLocation textureId) {
        this.textureId = textureId;
        this.active = false;
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, TransitionMode transitionMode) {
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		float fade = transitionMode.getFade(deltaTracker.getGameTimeDeltaPartialTick(true));
		RenderSystem.setShaderColor(fade, fade, fade, 1.0F);
		graphics.blit(this.textureId, 0, 0, -90, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
    }

    @Override
    public void tick(boolean active, double divisor, TransitionMode transitionMode) {
        if (active) {
            this.active = active;
        }
    }
}

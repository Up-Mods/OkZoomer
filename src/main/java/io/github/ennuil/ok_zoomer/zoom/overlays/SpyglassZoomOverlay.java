package io.github.ennuil.ok_zoomer.zoom.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ennuil.ok_zoomer.zoom.transitions.TransitionMode;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

// An implementation of the spyglass overlay as a zoom overlay
public class SpyglassZoomOverlay implements ZoomOverlay {
    private final ResourceLocation textureId;
    //private Minecraft minecraft;
    private float scale;
    private boolean active;

    public SpyglassZoomOverlay(ResourceLocation textureId) {
        this.textureId = textureId;
        this.scale = 0.5F;
        this.active = false;
		//this.minecraft = Minecraft.getInstance();
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public boolean cancelOverlayRendering() {
        return true;
    }

	// TODO - Consider whenever a third-person view block tweak option is desirable
    @Override
    public void renderOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, TransitionMode transitionMode) {
        int guiWidth = graphics.guiWidth();
        int guiHeight = graphics.guiHeight();
		float smallerLength = (float) Math.min(guiWidth, guiHeight);
		float scaledSmallerLength = Math.min((float) guiWidth / smallerLength, (float) guiHeight / smallerLength) * scale;
		int width = Mth.floor(smallerLength * scaledSmallerLength);
		int height = Mth.floor(smallerLength * scaledSmallerLength);
		int x = (guiWidth - width) / 2;
		int y = (guiHeight - height) / 2;
		int borderX = x + width;
		int borderY = y + height;
		RenderSystem.enableBlend();
		graphics.blit(textureId, x, y, -90, 0.0F, 0.0F, width, height, width, height);
		RenderSystem.disableBlend();
		graphics.fill(RenderType.guiOverlay(), 0, borderY, guiWidth, guiHeight, -90, CommonColors.BLACK);
		graphics.fill(RenderType.guiOverlay(), 0, 0, guiWidth, y, -90, CommonColors.BLACK);
		graphics.fill(RenderType.guiOverlay(), 0, y, x, borderY, -90, CommonColors.BLACK);
		graphics.fill(RenderType.guiOverlay(), borderX, y, guiWidth, borderY, -90, CommonColors.BLACK);
    }

    @Override
    public void tick(boolean active, double divisor, TransitionMode transitionMode) {
        this.active = active;
    }

    @Override
    public void tickBeforeRender(DeltaTracker deltaTracker) {
		if (!this.active) {
			this.scale = 0.5F;
		} else {
			this.scale = Mth.lerp(0.5F * deltaTracker.getGameTimeDeltaTicks(), this.scale, 1.125F);
		}
    }
}

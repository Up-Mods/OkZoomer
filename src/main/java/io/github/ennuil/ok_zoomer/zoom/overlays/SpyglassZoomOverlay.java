package io.github.ennuil.ok_zoomer.zoom.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

/**
 * An implementation of the spyglass overlay as a zoom overlay
 */
public class SpyglassZoomOverlay implements ZoomOverlay {
    private final ResourceLocation textureId;
    private Minecraft minecraft;
    private float scale;
    private boolean active;

    /**
     * Initializes an instance of the spyglass mouse modifier with the specified texture identifier
     *
	 * @param textureId the texture identifier for the spyglass overlay
    */
    public SpyglassZoomOverlay(ResourceLocation textureId) {
        this.textureId = textureId;
        this.scale = 0.5F;
        this.active = false;
		this.ensureClient();
    }

    @Override
    public boolean getActive() {
        return this.active;
    }

    @Override
    public boolean cancelOverlayRendering() {
        return true;
    }

    @Override
    public void renderOverlay(GuiGraphics graphics) {
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
    public void tick(boolean active, double divisor, double transitionMultiplier) {
        this.active = active;
    }

    @Override
    public void tickBeforeRender() {
		this.ensureClient();
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if (!this.active) {
                this.scale = 0.5F;
            } else {
                float lastFrameDuration = this.minecraft.getTimer().getGameTimeDeltaTicks();
                this.scale = Mth.lerp(0.5F * lastFrameDuration, this.scale, 1.125F);
            }
        }
    }

	private void ensureClient() {
		if (this.minecraft == null) {
			this.minecraft = Minecraft.getInstance();
		}
	}
}

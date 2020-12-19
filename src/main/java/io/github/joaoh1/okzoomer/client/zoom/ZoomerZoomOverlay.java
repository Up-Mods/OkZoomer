package io.github.joaoh1.okzoomer.client.zoom;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.joaoh1.libzoomer.api.ZoomOverlay;
import io.github.joaoh1.okzoomer.client.config.OkZoomerConfigPojo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ZoomerZoomOverlay implements ZoomOverlay {
    private Identifier OVERLAY_ID = new Identifier("okzoomer:zoom_overlay");
    private Identifier OVERLAY_TEXTURE_ID = new Identifier("okzoomer:textures/misc/zoom_overlay.png");
    private boolean active;
    private MinecraftClient client;
    private float overlayMultiplier;

    public ZoomerZoomOverlay() {
        this.active = false;
        this.client = MinecraftClient.getInstance();
        this.overlayMultiplier = 0.0F;
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
    public MinecraftClient setClient(MinecraftClient newClient) {
        return this.client;
    }

    @Override
    public void renderOverlay() {
        RenderSystem.disableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.overlayMultiplier);
		this.client.getTextureManager().bindTexture(OVERLAY_TEXTURE_ID);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(0.0D, (double)this.client.getWindow().getScaledHeight(), -90.0D).texture(0.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.client.getWindow().getScaledWidth(), (double)this.client.getWindow().getScaledHeight(), -90.0D).texture(1.0F, 1.0F).next();
		bufferBuilder.vertex((double)this.client.getWindow().getScaledWidth(), 0.0D, -90.0D).texture(1.0F, 0.0F).next();
		bufferBuilder.vertex(0.0D, 0.0D, -90.0D).texture(0.0F, 0.0F).next();
		tessellator.draw();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.enableAlphaTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void tick(boolean active, double divisor, double transitionMultiplier) {
        this.active = active;
        this.overlayMultiplier = (float) transitionMultiplier;
        if (this.client.options.hudHidden) {
            if (OkZoomerConfigPojo.tweaks.hideZoomOverlay) {
                return;
            }
        }
    }
}

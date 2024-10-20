package io.github.ennuil.ok_zoomer.zoom.overlays;

import io.github.ennuil.ok_zoomer.zoom.transitions.TransitionMode;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

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
		float fade = transitionMode.getFade(deltaTracker.getGameTimeDeltaPartialTick(true));
		int color = ARGB.colorFromFloat(1.0F, fade, fade, fade);
		graphics.blit(RenderType::vignette, this.textureId, 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight(), color);
	}

	@Override
	public void tick(boolean active, double divisor, TransitionMode transitionMode) {
		if (active || !transitionMode.getActive()) {
			this.active = active;
		}
	}
}

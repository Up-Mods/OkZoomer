package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

// Implements the zoom overlay
public class ZoomerZoomOverlay implements ZoomOverlay {
	private final Identifier textureId;
	private boolean active;

	public ZoomerZoomOverlay(Identifier textureId) {
		this.textureId = textureId;
		this.active = false;
	}

	@Override
	public boolean getActive() {
		return this.active;
	}

	@Override
	public void extractOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, EasedTransitionMode transitionMode) {
		float fade = transitionMode.getFade(deltaTracker.getGameTimeDeltaPartialTick(true));
		int color = ARGB.colorFromFloat(1.0F, fade, fade, fade);
		graphics.blit(RenderPipelines.VIGNETTE, this.textureId, 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight(), color);
	}

	@Override
	public void tick(boolean active, double divisor, EasedTransitionMode transitionMode) {
		if (active || !transitionMode.getActive()) {
			this.active = active;
		}
	}
}

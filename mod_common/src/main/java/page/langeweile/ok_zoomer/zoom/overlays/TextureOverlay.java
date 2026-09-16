package page.langeweile.ok_zoomer.zoom.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import page.langeweile.ok_zoomer.zoom.transitions.EasedTransitionMode;

public class TextureOverlay implements Overlay {
	private final Identifier textureId;
	private boolean active;

	public TextureOverlay(Identifier textureId) {
		this.textureId = textureId;
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
	public void extractOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, EasedTransitionMode transitionMode) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, textureId, 0, 0, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
	}

	@Override
	public void tick(boolean active, boolean transitionActive) {
		this.active = active;
	}
}

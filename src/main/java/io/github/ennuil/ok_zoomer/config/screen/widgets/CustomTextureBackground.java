package io.github.ennuil.ok_zoomer.config.screen.widgets;

import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public record CustomTextureBackground(ResourceLocation textureId, float red, float green, float blue, float alpha) implements Background {
	@Override
	public void render(GuiGraphics graphics, SpruceWidget widget, int vOffset, int mouseX, int mouseY, float delta) {
		int verticalOffset = vOffset;
		if (widget instanceof SpruceEntryListWidget<?> listWidget) {
			verticalOffset = (int) (listWidget.getScrollAmount());
		}

		this.renderBackgroundTexture(graphics, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), verticalOffset);
	}

	public void render(GuiGraphics graphics, Screen screen) {
		this.renderBackgroundTexture(graphics, 0, 0, screen.width, screen.height, 0);
	}

	public void renderBackgroundTexture(GuiGraphics graphics, int x, int y, int width, int height, int vOffset) {
		graphics.setColor(red, green, blue, alpha);
		graphics.blit(this.textureId, x, y, 0, 0.0F, vOffset, width, height, 32, 32);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

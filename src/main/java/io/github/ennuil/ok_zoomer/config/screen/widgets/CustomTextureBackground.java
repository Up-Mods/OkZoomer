package io.github.ennuil.ok_zoomer.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public record CustomTextureBackground(Identifier textureId, float red, float green, float blue, float alpha) implements Background {
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

	public void render(GuiGraphics graphics, Screen screen, int vOffset) {
		this.renderBackgroundTexture(graphics, 0, 0, screen.width, screen.height, vOffset);
	}

	public void renderBackgroundTexture(GuiGraphics graphics, int x, int y, int width, int height, int vOffset) {
		graphics.setShaderColor(red, green, blue, alpha);
		graphics.drawTexture(this.textureId, x, y, 0, 0.0F, vOffset, width, height, 32, 32);
		graphics.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

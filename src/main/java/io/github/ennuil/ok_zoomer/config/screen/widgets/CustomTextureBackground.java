package io.github.ennuil.ok_zoomer.config.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public record CustomTextureBackground(Identifier textureId, float red, float green, float blue, float alpha) implements Background {
	@Override
	public void render(MatrixStack matrices, SpruceWidget widget, int vOffset, int mouseX, int mouseY, float delta) {
		int verticalOffset = vOffset;
		if (widget instanceof SpruceEntryListWidget<?> listWidget) {
			verticalOffset = (int) (listWidget.getScrollAmount());
		}

		this.renderBackgroundTexture(matrices, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), verticalOffset);
	}

	public void render(MatrixStack matrices, Screen screen) {
		this.renderBackgroundTexture(matrices, 0, 0, screen.width, screen.height, 0);
	}

	public void render(MatrixStack matrices, Screen screen, int vOffset) {
		this.renderBackgroundTexture(matrices, 0, 0, screen.width, screen.height, vOffset);
	}

	public void renderBackgroundTexture(MatrixStack matrices, int x, int y, int width, int height, int vOffset) {
		RenderSystem.setShaderTexture(0, this.textureId);
		RenderSystem.setShaderColor(red, green, blue, alpha);
		DrawableHelper.drawTexture(matrices, x, y, 0, 0.0F, vOffset, width, height, 32, 32);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}

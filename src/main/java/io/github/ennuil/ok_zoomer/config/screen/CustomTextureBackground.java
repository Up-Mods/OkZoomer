package io.github.ennuil.ok_zoomer.config.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;

import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public record CustomTextureBackground(Identifier textureId, int red, int green, int blue, int alpha) implements Background {
	@Override
	public void render(MatrixStack matrices, SpruceWidget widget, int vOffset, int mouseX, int mouseY, float delta) {
		int verticalOffset = vOffset;
		if (widget instanceof SpruceEntryListWidget<?> listWidget) {
			verticalOffset = (int)(listWidget.getScrollAmount());
		}
		this.renderBackgroundTexture(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(), verticalOffset);
	}

	public void render(MatrixStack matrices, Screen screen, int vOffset) {
		this.renderBackgroundTexture(0, 0, screen.width, screen.height, vOffset);
	}

	public void renderBackgroundTexture(int x, int y, int width, int height, int vOffset) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, this.textureId);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(x, y + height, 0.0).uv(0.0F, (height + vOffset) / 32.0F).color(this.red, this.green, this.blue, this.alpha).next();
		bufferBuilder.vertex(x + width, y + height, 0.0).uv(width / 32.0F, (height + vOffset) / 32.0F).color(this.red, this.green, this.blue, this.alpha).next();
		bufferBuilder.vertex(x + width, y, 0.0).uv(width / 32.0F, vOffset / 32.0F).color(this.red, this.green, this.blue, this.alpha).next();
		bufferBuilder.vertex(x, y, 0.0).uv(0.0F, vOffset / 32.0F).color(this.red, this.green, this.blue, this.alpha).next();
		tessellator.draw();
	}
}

package io.github.ennuil.ok_zoomer.config.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.CommonColors;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class LabelledTextFieldWidget extends TextFieldWidget {
	public LabelledTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text message) {
		super(textRenderer, x, y, width, height, message);
	}

	@Override
	public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		// TODO - Use the text field's textRenderer instance through an accessor
		graphics.drawShadowedText(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX(), this.getY() + 1, CommonColors.WHITE);
		super.drawWidget(graphics, mouseX, mouseY, delta);
	}
}

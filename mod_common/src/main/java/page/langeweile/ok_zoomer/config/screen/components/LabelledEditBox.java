package page.langeweile.ok_zoomer.config.screen.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import page.langeweile.ok_zoomer.mixin.common.EditBoxAccessor;

public class LabelledEditBox extends EditBox {
	public LabelledEditBox(Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		graphics.drawString(((EditBoxAccessor) this).getFont(), this.getMessage(), this.getX(), this.getY() + 1, CommonColors.WHITE);
		super.renderWidget(graphics, mouseX, mouseY, delta);
	}
}

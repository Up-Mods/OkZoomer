package io.github.ennuil.ok_zoomer.config.screen.v2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.CommonColors;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.List;

@ClientOnly
public class OkZoomerEntryListWidget extends AbstractParentElement implements Drawable, Selectable {
	private final MinecraftClient client;
	private final List<Entry> children;
	private List<Integer> entryHeights;

	protected int width;
	protected int height;
	protected int x;
	protected int y;
	private final int contentWidth = 220;
	private int contentHeight;
	private int scrollAmount;
	private boolean scrolling;

	public OkZoomerEntryListWidget(MinecraftClient client, int width, int height, int x, int y) {
		this.client = client;
		this.children = new ArrayList<>();
		this.entryHeights = new ArrayList<>();
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;

		this.contentHeight = this.height;

		this.scrollAmount = 0;

		this.scrolling = false;

		for (int i = 0; i < 25; i++) {
			this.children.add(new ButtonEntry());
			this.children.add(new TextEntry());
		}

		this.update();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics);

		graphics.enableScissor(this.x, this.y, this.width + this.x, this.height + this.y);
		int i = this.y - this.scrollAmount;
		for (var child : children) {
			int oldI = i;
			i += child.getEntryHeight();
			if (i >= this.y && oldI <= this.height + this.y) {
				child.render(graphics, this.x, oldI, mouseX, mouseY, delta);
			}
		}
		graphics.disableScissor();

		graphics.drawText(this.client.textRenderer, "scrolly:" + this.scrollAmount, (this.x + this.width) / 2, this.y, CommonColors.WHITE, true);

		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.y, this.width + this.x, this.y + 4, CommonColors.BLACK, 0x00000000, 0);
		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.height + this.y - 4, this.width + this.x, this.height + this.y, 0x00000000, CommonColors.BLACK, 0);

		this.renderScrollBar(graphics);
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {

	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	@Override
	public SelectionType getType() {
		return SelectionType.HOVERED;
	}

	private void renderBackground(GuiGraphics graphics) {
		graphics.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
		graphics.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, this.x, this.y, this.width - x, this.height - y + this.scrollAmount, this.width, this.height, 32, 32);
		graphics.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderScrollBar(GuiGraphics graphics) {
		int size = Math.min(this.height, (this.height * this.height) / this.contentHeight);
		int pos = (this.width - x) / 2 + 156;

		var aaa = (scrollAmount / (double) (this.contentHeight - this.height));
		var z = this.y + (int) (aaa * (this.height - size));

		graphics.fill(pos, this.y, pos + 6, this.y + this.height, CommonColors.BLACK);
		graphics.fill(pos, z, pos + 6, size + z, 0xFF808080);
		graphics.fill(pos, z, pos + 6 - 1, size + z - 1, 0xFFC0C0C0);
	}

	public void update() {
		this.entryHeights = new ArrayList<>();
		int contentHeight = 0;

		for (var child : this.children) {
			entryHeights.add(child.getEntryHeight());
			contentHeight += child.getEntryHeight();
		}

		this.contentHeight = Math.max(this.height, contentHeight);
	}

	public int getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(int scrollAmount) {
		this.scrollAmount = MathHelper.clamp(scrollAmount, 0, Math.max(0, this.contentHeight - this.height));
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.scrolling) {
			int pos = (this.width - x) / 2 + 156;
			if (mouseX > pos) {
				this.scrolling = true;
				return true;
			}
		}

		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		this.setScrollAmount((int) (this.scrollAmount - amount * 10));
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_1 && this.scrolling) {
			if (mouseY < this.y) {
				this.setScrollAmount(0);
			} else if (mouseY > this.y + this.height) {
				this.setScrollAmount(this.contentHeight);
			} else {
				// TODO - Complete this
				int size = MathHelper.clamp((this.height * this.height) / this.contentHeight, 0, this.height - 6);

				double a = Math.max(1.0, ((double) this.contentHeight / (this.height - size)));
				this.setScrollAmount(this.getScrollAmount() + (int) (deltaY * a));
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.scrolling) {
			this.scrolling = false;
			return true;
		} else {
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}

	@Override
	public void setFocusedChild(@Nullable Element child) {
		super.setFocusedChild(child);

		int i = this.children.indexOf(child);
		if (i >= 0) {
			var entry = this.children.get(i);
			if (this.client.getLastInputType().isKeyboard()) {
				// TODO - autoscroll
			}
		}
	}

	@ClientOnly
	public abstract class Entry implements ParentElement {
		@Nullable
		private Element focused;
		private boolean dragging;

		public abstract void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta);

		public abstract int getEntryHeight();

		@Override
		public boolean isDragging() {
			return this.dragging;
		}

		@Override
		public void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		@Override
		public boolean isFocused() {
			return OkZoomerEntryListWidget.this.getFocused() == this;
		}

		@Override
		public void setFocusedChild(@Nullable Element child) {
			if (this.focused != null) {
				this.focused.setFocused(false);
			}

			if (child != null) {
				child.setFocused(true);
			}

			this.focused = child;
		}
	}

	@ClientOnly
	class TextEntry extends Entry {
		@Override
		public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
			graphics.drawText(OkZoomerEntryListWidget.this.client.textRenderer, "aeiou", x, y, CommonColors.WHITE, true);
			graphics.drawText(OkZoomerEntryListWidget.this.client.textRenderer, "aaaaa", x, y + 10, CommonColors.WHITE, true);
		}

		@Override
		public int getEntryHeight() {
			return 25;
		}

		@Override
		public List<? extends Element> children() {
			return List.of();
		}
	}

	@ClientOnly
	class ButtonEntry extends Entry {
		private final ButtonWidget testButton;
		private final ButtonWidget testButton2;

		public ButtonEntry() {
			this.testButton = ButtonWidget.builder(Text.literal("Can we?"), button -> {})
				.positionAndSize(0, 0, 60, 20)
				.build();
			this.testButton2 = ButtonWidget.builder(Text.literal("Can we?"), button -> {})
					.positionAndSize(0, 0, 60, 20)
					.build();
		}

		@Override
		public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
			this.testButton.setPosition(x, y);
			this.testButton.render(graphics, mouseX, mouseY, delta);
			this.testButton2.setPosition(x, y + 20);
			this.testButton2.render(graphics, mouseX, mouseY, delta);
		}

		@Override
		public int getEntryHeight() {
			return 40;
		}

		@Override
		public List<? extends Element> children() {
			return List.of(testButton, testButton2);
		}
	}
}

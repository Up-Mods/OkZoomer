package io.github.ennuil.ok_zoomer.config.screen;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenArea;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
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
	private IntList entryHeights;

	protected int width;
	protected int height;
	protected int x;
	protected int y;
	private int contentHeight;
	private int scrollAmount;
	private boolean scrolling;

	public OkZoomerEntryListWidget(MinecraftClient client, int width, int height, int x, int y) {
		this.client = client;
		this.children = new ArrayList<>();
		this.entryHeights = new IntArrayList();
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;

		this.contentHeight = this.height;

		this.scrollAmount = 0;

		this.scrolling = false;

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
				int xToRender = this.x + this.width / 2 - this.getRowWidth() / 2;
				child.render(graphics, xToRender, oldI, this.getRowWidth(), mouseX, mouseY, delta);
			}
		}
		graphics.disableScissor();

		graphics.drawText(this.client.textRenderer, "scroll:" + this.scrollAmount, this.x + this.width / 2 + this.getRowWidth() / 2, this.y, CommonColors.WHITE, true);

		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.y, this.width + this.x, this.y + 4, CommonColors.BLACK, 0x00000000, 0);
		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.height + this.y - 4, this.width + this.x, this.height + this.y, 0x00000000, CommonColors.BLACK, 0);

		if (this.contentHeight - this.height > 0) {
			this.renderScrollBar(graphics);
		}
	}

	// TODO - SpruceUI sucks at narration too, but You Can Do Different
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
		if (this.client.world == null) {
			graphics.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
			graphics.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, this.x, this.y, this.width - x, this.height - y + this.scrollAmount, this.width, this.height, 32, 32);
			graphics.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			//graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x80000000);
			graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x60000000);
		}
	}

	private void renderScrollBar(GuiGraphics graphics) {
		int size = Math.min(this.height, (this.height * this.height) / this.contentHeight);
		int pos = (this.width - x) / 2 + 156;

		var aaa = (this.scrollAmount / (double) (this.contentHeight - this.height));
		var z = this.y + (int) (aaa * (this.height - size));

		graphics.fill(pos, this.y, pos + 6, this.y + this.height, CommonColors.BLACK);
		graphics.fill(pos, z, pos + 6, size + z, 0xFF808080);
		graphics.fill(pos, z, pos + 6 - 1, size + z - 1, 0xFFC0C0C0);
	}

	public void update() {
		this.entryHeights = new IntArrayList();
		int contentHeight = 0;

		for (var child : this.children) {
			entryHeights.add(child.getEntryHeight());
			contentHeight += child.getEntryHeight();
		}

		this.contentHeight = Math.max(this.height, contentHeight);
	}

	public void finish() {
		this.update();
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
			int pos = (this.width - this.x) / 2 + 156;
			if (mouseX > pos && mouseX < pos + 6) {
				this.scrolling = true;
				return true;
			}
		}

		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			var entry = this.getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					var subEntry = this.getFocused();
					if (subEntry != entry && subEntry instanceof ParentElement parentElement) {
						parentElement.setFocusedChild(null);
					}

					this.setFocusedChild(entry);
					this.setDragging(true);
					return true;
				}
			}
		}

		return this.scrolling;
		//return super.mouseClicked(mouseX, mouseY, button);
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
			if (this.client.getLastInputType().isKeyboard()) {
				this.ensureVisible(i);
			}
		}
	}

	@Override
	public ScreenArea getArea() {
		return new ScreenArea(this.x, this.y, this.width, this.height);
	}

	protected int getEntryHeightSum(int index) {
		int sum = 0;
		for (int i = 0; i < index; i++) {
			sum += this.entryHeights.getInt(i);
		}

		return sum;
	}

	protected int getRowTop(int index) {
		return this.y + 4 - this.getScrollAmount() + getEntryHeightSum(index);
	}

	public int getRowWidth() {
		return 310;
	}

	protected void ensureVisible(int index) {
		int rowTop = this.getRowTop(index);
		int rowTop2 = rowTop - this.y - 4 - entryHeights.getInt(index);

		if (rowTop2 < 0) {
			this.setScrollAmount(this.getScrollAmount() + rowTop2);
		}

		int rowTop3 = (this.y + this.height) - rowTop - (entryHeights.getInt(index) * 2);

		if (rowTop3 < 0) {
			this.setScrollAmount(this.getScrollAmount() - rowTop3);
		}
	}

	// TODO - This faulty math is the faultiest math of all faulty maths! Fix this, you can do better than a racist
	protected final Entry getEntryAtPosition(double x, double y) {
		if (y < this.entryHeights.intStream().sum() - scrollAmount) {
			/*
			int rowCenter = this.getRowWidth() / 2;
			int absoluteCenter = this.x + this.width / 2;
			*/
			int sum = 0;
			int i = 0;
			while (sum <= (y - this.y) + this.scrollAmount) {
				sum += this.entryHeights.getInt(i);
				i++;
			}
			i--;

			System.out.println(i + " - " + y);

			if (i < this.children.size()) {
				return this.children.get(i);
			}
		}

		return null;
	}

	public void addCategory(Text text) {
		this.children.add(new CategoryEntry(text));
	}

	public void addButton(ClickableWidget button) {
		this.children.add(new ButtonEntry(button));
	}

	public void addButton(ClickableWidget leftButton, ClickableWidget rightButton) {
		this.children.add(new ButtonEntry(leftButton, rightButton));
	}

	public void addServerEffectEntry(Text text) {
		this.children.add(new ServerEffectEntry(text));
	}

	@ClientOnly
	public abstract class Entry implements ParentElement {
		@Nullable
		private Element focused;
		private boolean dragging;

		public abstract void render(GuiGraphics graphics, int x, int y, int rowWidth, int mouseX, int mouseY, float delta);

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

	// TODO - Use MultilineTextWidget
	@ClientOnly
	class CategoryEntry extends Entry {
		private final Text title;

		private CategoryEntry(Text title) {
			this.title = title;
		}

		@Override
		public void render(GuiGraphics graphics, int x, int y, int rowWidth, int mouseX, int mouseY, float delta) {
			graphics.fill(x, y + 1, x + rowWidth, y + 19, 0x80000000);
			graphics.drawCenteredShadowedText(OkZoomerEntryListWidget.this.client.textRenderer, this.title, x + rowWidth / 2, y + 6, CommonColors.WHITE);
		}

		@Override
		public int getEntryHeight() {
			return 20;
		}

		// TODO - Use the KeyBindListWidget.CategoryEntry code for narrator-friendly categories
		@Override
		public List<? extends Element> children() {
			return List.of();
		}
	}

	@ClientOnly
	class ButtonEntry extends Entry {
		private final ClickableWidget leftButton;
		private final ClickableWidget rightButton;

		public ButtonEntry(ClickableWidget button) {
			button.setWidth(310);
			this.leftButton = button;
			this.rightButton = null;
		}

		public ButtonEntry(ClickableWidget leftButton, ClickableWidget rightButton) {
			this.leftButton = leftButton;
			this.rightButton = rightButton;
		}

		@Override
		public void render(GuiGraphics graphics, int x, int y, int rowWidth, int mouseX, int mouseY, float delta) {
			this.leftButton.setPosition(x, y + 2);
			this.leftButton.render(graphics, mouseX, mouseY, delta);

			if (this.rightButton != null) {
				this.rightButton.setPosition(x + 160, y + 2);
				this.rightButton.render(graphics, mouseX, mouseY, delta);
			}
		}

		// Yes, I don't exactly like this either, but this allows for gaps of 5 pixels as well as a nice bottom padding
		// against the end of the page
		@Override
		public int getEntryHeight() {
			return 24;
		}

		// TODO - Create a list variable
		@Override
		public List<? extends Element> children() {
			return rightButton != null ? List.of(leftButton, rightButton) : List.of(leftButton);
		}
	}

	@ClientOnly
	class ServerEffectEntry extends Entry {
		private final MultilineText serverEffect;
		private int lines = 16;

		private ServerEffectEntry(Text serverEffect) {
			this.serverEffect = MultilineText.create(OkZoomerEntryListWidget.this.client.textRenderer, serverEffect, 310);
		}

		@Override
		public void render(GuiGraphics graphics, int x, int y, int rowWidth, int mouseX, int mouseY, float delta) {
			this.lines = this.serverEffect.render(graphics, x + rowWidth / 2, y + 4, 9, CommonColors.GRAY) - y + 3;
		}

		@Override
		public int getEntryHeight() {
			return lines;
		}

		// TODO - Use the KeyBindListWidget.CategoryEntry code for narrator-friendly categories
		@Override
		public List<? extends Element> children() {
			return List.of();
		}
	}
}

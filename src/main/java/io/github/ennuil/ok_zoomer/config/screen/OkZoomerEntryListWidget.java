package io.github.ennuil.ok_zoomer.config.screen;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigationEvent;
import net.minecraft.client.gui.screen.NavigationAxis;
import net.minecraft.client.gui.screen.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenArea;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
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
import java.util.function.Predicate;

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
	@Nullable
	private Entry hoveredEntry;

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

		this.hoveredEntry = null;

		this.update();
	}

	@Nullable
	public Entry getFocused() {
		return (Entry) super.getFocused();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics);
		this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

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

		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.y, this.width + this.x, this.y + 4, CommonColors.BLACK, 0x00000000, 0);
		graphics.fillGradient(RenderLayer.getGuiOverlay(), this.x, this.height + this.y - 4, this.width + this.x, this.height + this.y, 0x00000000, CommonColors.BLACK, 0);

		if (this.contentHeight - this.height > 0) {
			this.renderScrollBar(graphics);
		}
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		var hoveredEntry = this.getHoveredEntry();
		if (hoveredEntry != null) {
			hoveredEntry.appendNarrations(builder.nextMessage());
			this.appendNarrations(builder, hoveredEntry);
		} else {
			var entry = this.getFocused();
			if (entry != null) {
				entry.appendNarrations(builder.nextMessage());
				this.appendNarrations(builder, entry);
			}
		}

		builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
	}

	protected void appendNarrations(NarrationMessageBuilder builder, Entry entry) {
		List<Entry> list = this.children();
		if (!list.isEmpty()) {
			int i = list.indexOf(entry);
			if (i != -1) {
				builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.list", i + 1, list.size()));
			}
		}
	}

	@Override
	public List<Entry> children() {
		return this.children;
	}

	@Override
	public SelectionType getType() {
		if (this.isFocused()) {
			return SelectionType.FOCUSED;
		} else {
			return this.hoveredEntry != null ? SelectionType.HOVERED : SelectionType.NONE;
		}
	}

	private void renderBackground(GuiGraphics graphics) {
		if (this.client.world == null) {
			graphics.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
			graphics.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, this.x, this.y, this.width - x, this.height - y + this.scrollAmount, this.width, this.height, 32, 32);
			graphics.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0x60000000);
		}
	}

	private void renderScrollBar(GuiGraphics graphics) {
		int size = Math.min(this.height, (this.height * this.height) / this.contentHeight);
		int x = this.getScrollBarPosX();

		var scale = (this.scrollAmount / (double) (this.contentHeight - this.height));
		var y = this.y + (int) (scale * (this.height - size));

		graphics.fill(x, this.y, x + 6, this.y + this.height, CommonColors.BLACK);
		graphics.fill(x, y, x + 6, size + y, 0xFF808080);
		graphics.fill(x, y, x + 6 - 1, size + y - 1, 0xFFC0C0C0);
	}

	protected int getScrollBarPosX() {
		return this.width / 2 + 156;
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
					if (subEntry != entry && subEntry != null) {
						subEntry.setFocusedChild(null);
					}

					this.setFocusedChild(entry);
					this.setDragging(true);
					return true;
				}
			}
		}

		return this.scrolling;
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
				int size = MathHelper.clamp((this.height * this.height) / this.contentHeight, 0, this.height - 6);
				double scale = Math.max(1.0, ((double) this.contentHeight / (this.height - size)));
				this.setScrollAmount(this.getScrollAmount() + (int) (deltaY * scale));
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

	// This is so faithful to Vanilla's algo that it also inherits the Bottom Void Pixel of Doom! Oh no!
	protected final Entry getEntryAtPosition(double x, double y) {
		int center = this.x + this.width / 2;
		int halfRowWidth = this.getRowWidth() / 2;
		int rowMinX = center - halfRowWidth;
		int rowMaxX = center + halfRowWidth;

		int sum = 0;
		int i = 0;

		while (sum <= MathHelper.floor(y - this.y) + this.scrollAmount) {
			if (i < this.entryHeights.size()) {
				sum += this.entryHeights.getInt(i);
				i++;
			} else {
				i++;
				break;
			}
		}
		i--;

		if (x < this.getScrollBarPosX() && x >= rowMinX && x <= rowMaxX && i < this.children.size()) {
			return this.children.get(i);
		}

		return null;
	}

	/*
	@Nullable
	protected Entry nextEntry(NavigationDirection direction) {
		return this.nextEntry(direction, e -> true);
	}

	@Nullable
	protected Entry nextEntry(NavigationDirection direction, Predicate<Entry> predicate) {
		return this.nextEntry(direction, predicate, (Entry) this.getFocused());
	}
	*/

	@Nullable
	protected Entry nextEntry(NavigationDirection direction, Predicate<Entry> predicate, @Nullable Entry currentEntry) {
		int i = switch (direction) {
			case LEFT, RIGHT -> 0;
			case UP -> -1;
			case DOWN -> 1;
		};

		if (!this.children.isEmpty() && i != 0) {
			int j;
			if (currentEntry == null) {
				j = i > 0 ? 0 : this.children.size() - 1;
			} else {
				j = this.children.indexOf(currentEntry) + i;
			}

			for (int k = j; k >= 0 && k < this.children.size(); k += i) {
				var entry = this.children.get(k);
				if (predicate.test(entry)) {
					return entry;
				}
			}
		}


		return null;
	}

	@Nullable
	@Override
	public ElementPath nextFocusPath(GuiNavigationEvent event) {
		if (this.children.isEmpty()) {
			return null;
		} else if (!(event instanceof GuiNavigationEvent.TabNavigation tabNav)) {
			return super.nextFocusPath(event);
		} else {
			var entry = (Entry) this.getFocused();

			if (tabNav.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
				return ElementPath.createPath(this, entry.nextFocusPath(event));
			} else {
				int i = -1;
				var navDir = tabNav.direction();
				if (entry != null) {
					i = entry.children().indexOf(entry.getFocused());
				}

				if (i == -1) {
					switch (navDir) {
						case LEFT -> {
							i = Integer.MAX_VALUE;
							navDir = NavigationDirection.DOWN;
						}
						case RIGHT -> {
							i = 0;
							navDir = NavigationDirection.DOWN;
						}
						default -> i = 0;
					}
				}

				var entry2 = entry;
				ElementPath path = null;

				while (path == null) {
					entry2 = this.nextEntry(navDir, entryx -> !entryx.children().isEmpty(), entry2);
					if (entry2 == null) {
						return null;
					}

					path = entry2.getFocusPathAtIndex(tabNav, i);
				}

				return ElementPath.createPath(this, path);
			}
		}
	}

	public @Nullable Entry getHoveredEntry() {
		return this.hoveredEntry;
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
		@Nullable
		private Selectable focusedSelectable;
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

		@Nullable
		public ElementPath getFocusPathAtIndex(GuiNavigationEvent event, int index) {
			if (this.children().isEmpty()) {
				return null;
			} else {
				var path = (this.children().get(Math.min(index, this.children().size() - 1))).nextFocusPath(event);
				return ElementPath.createPath(this, path);
			}
		}

		@Nullable
		@Override
		public ElementPath nextFocusPath(GuiNavigationEvent event) {
			if (event instanceof GuiNavigationEvent.TabNavigation tabNavigation) {
				int i = switch (tabNavigation.direction()) {
					case LEFT -> -1;
					case RIGHT -> 1;
					case UP, DOWN -> 0;
				};

				if (i == 0) return null;

				int j = MathHelper.clamp(i + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

				for (int k = j; k >= 0 && k < this.children().size(); k += i) {
					var element = (Element) this.children().get(k);
					var path = element.nextFocusPath(event);
					if (path != null) {
						return ElementPath.createPath(this, path);
					}
				}
			}

			return ParentElement.super.nextFocusPath(event);
		}

		void appendNarrations(NarrationMessageBuilder builder) {
			var list = this.selectableChildren();
			var narrationData = Screen.findSelectedElementData(list, this.focusedSelectable);
			if (narrationData != null) {
				if (narrationData.selectType.isFocused()) {
					this.focusedSelectable = narrationData.selectable;
				}

				if (!list.isEmpty()) {
					builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.object_list", narrationData.index + 1, list.size()));
					if (narrationData.selectType == Selectable.SelectionType.FOCUSED) {
						builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
					}
				}

				narrationData.selectable.appendNarrations(builder.nextMessage());
			}
		}

		public abstract List<? extends Selectable> selectableChildren();
	}

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
		public @Nullable ElementPath nextFocusPath(GuiNavigationEvent event) {
			return null;
		}

		@Override
		public int getEntryHeight() {
			return 20;
		}

		@Override
		public List<? extends Element> children() {
			return List.of();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(new Selectable() {
				@Override
				public SelectionType getType() {
					return SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, CategoryEntry.this.title);
				}
			});
		}
	}

	@ClientOnly
	class ButtonEntry extends Entry {
		private final ClickableWidget leftButton;
		private final ClickableWidget rightButton;
		private final int entryHeight;
		private final List<ClickableWidget> buttons;

		public ButtonEntry(ClickableWidget button) {
			button.setWidth(310);
			this.leftButton = button;
			this.rightButton = null;
			this.entryHeight = button.getHeight() + 4;
			this.buttons = List.of(button);
		}

		public ButtonEntry(ClickableWidget leftButton, ClickableWidget rightButton) {
			this.leftButton = leftButton;
			this.rightButton = rightButton;
			this.entryHeight = (rightButton != null ? Math.max(leftButton.getHeight(), rightButton.getHeight()) : leftButton.getHeight()) + 4;
			this.buttons = rightButton != null ? List.of(leftButton, rightButton) : List.of(leftButton);
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
		// (This used to be a hardcoded reference to 24)
		@Override
		public int getEntryHeight() {
			return this.entryHeight;
		}

		@Override
		public List<? extends Element> children() {
			return this.buttons;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.buttons;
		}
	}

	@ClientOnly
	class ServerEffectEntry extends Entry {
		private final MultilineText serverEffect;
		private final Text serverEffectText;
		private int lines = 16;

		private ServerEffectEntry(Text serverEffect) {
			this.serverEffect = MultilineText.create(OkZoomerEntryListWidget.this.client.textRenderer, serverEffect, 310);
			this.serverEffectText = serverEffect;
		}

		@Override
		public void render(GuiGraphics graphics, int x, int y, int rowWidth, int mouseX, int mouseY, float delta) {
			this.lines = this.serverEffect.render(graphics, x + rowWidth / 2, y + 4, 9, CommonColors.GRAY) - y + 3;
		}

		@Override
		public @Nullable ElementPath nextFocusPath(GuiNavigationEvent event) {
			return null;
		}

		@Override
		public int getEntryHeight() {
			return lines;
		}

		@Override
		public List<? extends Element> children() {
			return List.of();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return List.of(new Selectable() {
				@Override
				public SelectionType getType() {
					return SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, ServerEffectEntry.this.serverEffectText);
				}
			});
		}
	}
}

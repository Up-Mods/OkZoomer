package page.langeweile.ok_zoomer.config.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OkZoomerSelectionList extends ContainerObjectSelectionList<OkZoomerSelectionList.Entry> {
	private final Screen screen;

	public OkZoomerSelectionList(Minecraft minecraft, int width, int height, int y, Screen screen) {
		super(minecraft, width, height, y, 25);
		this.screen = screen;
	}

	@Override
	public int getRowWidth() {
		return 310;
	}

	public void addCategory(Component component) {
		int baseline = 9;
		int padding = this.children().isEmpty() ? 0 : baseline * 2;
		this.addEntry(new CategoryEntry(component, this.screen, padding), padding + baseline + 4);
	}

	public void addButton(AbstractWidget button) {
		this.addEntry(new ButtonEntry(button, this.screen));
	}

	public void addButton(AbstractWidget leftButton, AbstractWidget rightButton) {
		this.addEntry(new ButtonEntry(leftButton, rightButton, this.screen));
	}

	public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		final Screen screen;

		Entry(Screen screen) {
			this.screen = screen;
		}
	}

	static class CategoryEntry extends Entry {
		private final int paddingTop;
		private final StringWidget widget;

		private CategoryEntry(Component title, Screen screen, int paddingTop) {
			super(screen);
			this.widget = new StringWidget(title, screen.getFont());
			this.paddingTop = paddingTop;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			this.widget.setPosition(this.screen.width / 2 - 155, this.getContentY() + this.paddingTop);
			this.widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return List.of(this.widget);
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
			return List.of(this.widget);
		}
	}

	static class ButtonEntry extends Entry {
		private final AbstractWidget leftButton;
		private final AbstractWidget rightButton;
		private final List<AbstractWidget> buttons;

		public ButtonEntry(AbstractWidget button, Screen screen) {
			super(screen);
			button.setWidth(310);
			this.leftButton = button;
			this.rightButton = null;
			this.buttons = List.of(button);
		}

		public ButtonEntry(AbstractWidget leftButton, AbstractWidget rightButton, Screen screen) {
			super(screen);
			this.leftButton = leftButton;
			this.rightButton = rightButton;
			this.buttons = rightButton != null ? List.of(leftButton, rightButton) : List.of(leftButton);
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			int x = this.screen.width / 2 - 155;
			int y = this.getContentY();

			this.leftButton.setPosition(x, y + 2);
			this.leftButton.extractRenderState(graphics, mouseX, mouseY, partialTick);

			if (this.rightButton != null) {
				this.rightButton.setPosition(x + 160, y + 2);
				this.rightButton.extractRenderState(graphics, mouseX, mouseY, partialTick);
			}
		}

		@Override
		public int getHeight() {
			return (rightButton != null ? Math.max(leftButton.getHeight(), rightButton.getHeight()) : leftButton.getHeight()) + 4;
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return this.buttons;
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return this.buttons;
		}
	}
}

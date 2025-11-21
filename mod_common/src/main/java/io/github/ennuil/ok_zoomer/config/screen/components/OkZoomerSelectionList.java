package io.github.ennuil.ok_zoomer.config.screen.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
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
		this.addEntry(new CategoryEntry(component, this.screen));
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
		private final Component title;

		private CategoryEntry(Component title, Screen screen) {
			super(screen);
			this.title = title;
		}

		@Override
		public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			int x = this.screen.width / 2 - 155;
			int y = this.getContentY();
			int rowWidth = 310;

			graphics.fill(x, y + 1, x + rowWidth, y + 19, 0xA0000000);
			graphics.drawCenteredString(this.screen.getFont(), this.title, x + rowWidth / 2, y + 6, CommonColors.WHITE);
		}

		@Override
		public int getHeight() {
			return 20;
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return List.of();
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
			return List.of(new NarratableEntry() {
				@Override
				public @NotNull NarrationPriority narrationPriority() {
					return NarrationPriority.HOVERED;
				}

				@Override
				public void updateNarration(NarrationElementOutput narrationElementOutput) {
					narrationElementOutput.add(NarratedElementType.TITLE, CategoryEntry.this.title);
				}
			});
		}
	}

	static class ButtonEntry extends Entry {
		private final AbstractWidget leftButton;
		private final AbstractWidget rightButton;
		private final int entryHeight;
		private final List<AbstractWidget> buttons;

		public ButtonEntry(AbstractWidget button, Screen screen) {
			super(screen);
			button.setWidth(310);
			this.leftButton = button;
			this.rightButton = null;
			this.entryHeight = button.getHeight() + 4;
			this.buttons = List.of(button);
		}

		public ButtonEntry(AbstractWidget leftButton, AbstractWidget rightButton, Screen screen) {
			super(screen);
			this.leftButton = leftButton;
			this.rightButton = rightButton;
			this.entryHeight = (rightButton != null ? Math.max(leftButton.getHeight(), rightButton.getHeight()) : leftButton.getHeight()) + 4;
			this.buttons = rightButton != null ? List.of(leftButton, rightButton) : List.of(leftButton);
		}

		@Override
		public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			int x = this.screen.width / 2 - 155;
			int y = this.getContentY();
			int rowWidth = 310;

			this.leftButton.setPosition(x, y + 2);
			this.leftButton.render(graphics, mouseX, mouseY, partialTick);

			if (this.rightButton != null) {
				this.rightButton.setPosition(x + 160, y + 2);
				this.rightButton.render(graphics, mouseX, mouseY, partialTick);
			}
		}

		// Yes, I don't exactly like this either, but this allows for gaps of 5 pixels as well as a nice bottom padding
		// against the end of the page
		// (This used to be a hardcoded reference to 24)
		@Override
		public int getHeight() {
			return this.entryHeight;
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

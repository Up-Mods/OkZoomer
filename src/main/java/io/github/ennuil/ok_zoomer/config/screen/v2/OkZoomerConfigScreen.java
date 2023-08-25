package io.github.ennuil.ok_zoomer.config.screen.v2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Configs;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class OkZoomerConfigScreen extends Screen {
	private final Identifier configId;
	private final Screen parent;
	private Config config;
	private OkZoomerEntryListWidget a;

	public OkZoomerConfigScreen(Screen parent) {
		super(Text.of("Test!"));
		this.configId = new Identifier("ok_zoomer", "config");
		this.parent = new io.github.ennuil.ok_zoomer.config.screen.OkZoomerConfigScreen(parent);
	}

	@Override
	protected void init() {
		this.config = Configs.getConfig(this.configId.getNamespace(), this.configId.getPath());
		this.addDrawableChild(
			ButtonWidget.builder(CommonTexts.DONE, button -> this.client.setScreen(parent))
				.positionAndSize(this.width / 2 - 100, this.height - 27, 200, 20)
				.build());
		this.a = new OkZoomerEntryListWidget(this.client, this.width, this.height - 64, 0, 32);
		this.addSelectableChild(a);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics);
		graphics.drawCenteredShadowedText(this.textRenderer, ConfigTextUtils.getConfigTitle(configId), this.width / 2, 20, 0xFFFFFFFF);
		int i = 1;
		for (var a : this.config.nodes()) {
			i++;
			if (a instanceof TrackedValue<?> tracked) {
				graphics.drawCenteredShadowedText(this.textRenderer, a.key().getLastComponent() + " - " + tracked.value(), this.width / 2, 20 * i, 0xFFFFFFFF);
				if (tracked.value() instanceof Boolean) {

				}
			} else if (a instanceof ValueTreeNode.Section section) {
				graphics.drawCenteredShadowedText(this.textRenderer, a.key().getLastComponent() + " - " + section.key().toString(), this.width / 2, 20 * i, 0xFFFFFFFF);
			}
		}
		this.a.render(graphics, mouseX, mouseY, delta);
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.parent);
	}
}

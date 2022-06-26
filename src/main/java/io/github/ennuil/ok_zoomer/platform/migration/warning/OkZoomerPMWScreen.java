package io.github.ennuil.ok_zoomer.platform.migration.warning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.option.SpruceSeparatorOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.github.ennuil.ok_zoomer.config.screen.widgets.CustomTextureBackground;
import io.github.ennuil.ok_zoomer.config.screen.widgets.SpruceLabelOption;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.CustomValue.CvObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

// TODO - This screen should be a library mod
@SuppressWarnings("deprecation")
public class OkZoomerPMWScreen extends SpruceScreen {
	private final Screen parent;
	private SpruceOptionListWidget list;
	private CustomTextureBackground normalBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/purple_concrete.png"), 64, 64, 64, 255);
	private CustomTextureBackground darkenedBackground = new CustomTextureBackground(new Identifier("minecraft:textures/block/purple_concrete.png"), 32, 32, 32, 255);

	public OkZoomerPMWScreen(Screen parent) {
		super(Text.translatable("platform_migration_warning.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.list = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - 36 - 22);
		this.list.setBackground(this.darkenedBackground);

		var explainerLabel = new SpruceLabelOption("platform_migration_warning.explainer", true);
		this.list.addSingleOptionEntry(explainerLabel);

		Map<String, ModDeveloper> modDevelopers = new HashMap<>();
		FabricLoader.getInstance().getAllMods().stream().filter(mod -> mod.getMetadata().containsCustomValue("platform_migration_warning")).forEach(mod -> {
			CvObject pmw = mod.getMetadata().getCustomValue("platform_migration_warning").getAsObject();
			var modText = Text.literal(pmw.get("name").getAsString()).styled(style -> style.withColor(0xFFFFFF));
			var migratedSinceText = Text.translatable("platform_migration_warning.mod.migrated_since", Text.literal(pmw.get("migrated_since").getAsString()));
			var modLabel = new SpruceLabelOption("platform_migration_warning.id_" + mod.getMetadata().getId(), modText, true, migratedSinceText);
			this.list.addSingleOptionEntry(modLabel);

			String author = pmw.get("author").getAsString();
			if (!modDevelopers.containsKey(author)) {
				List<Text> modList = new ArrayList<>();
				modList.add(Text.literal(pmw.get("name").getAsString()));
				modDevelopers.put(author, new ModDeveloper(author, modList));
			} else {
				modDevelopers.get(author).mods().add(Text.literal(pmw.get("name").getAsString()));
			}
		});

		var explainerLabel2 = new SpruceLabelOption("platform_migration_warning.explainer2", true);
		this.list.addSingleOptionEntry(explainerLabel2);

		for (ModDeveloper modDeveloper : modDevelopers.values()) {
			String authorKey = String.format("platform_migration_warning.%s.author", modDeveloper.author);
			String testimonialKey = String.format("platform_migration_warning.%s.testimonial", modDeveloper.author);
			Text hoverText = switch (modDeveloper.mods.size()) {
				case 0 -> null;
				case 1 -> Text.translatable("platform_migration_warning.has_developed_1", modDeveloper.mods.get(0));
				case 2 -> Text.translatable("platform_migration_warning.has_developed_2", modDeveloper.mods.get(0), modDeveloper.mods.get(1));
				default -> {
					MutableText mutableText = Text.translatable("platform_migration_warning.has_developed_many");
					modDeveloper.mods().forEach(text -> mutableText.append(Text.translatable("platform_migration_warning.has_developed_many_entry", text)));
					yield mutableText;
				}
			};

			var restrictionsSeparator = new SpruceSeparatorOption(authorKey, true, hoverText);
			var testimonialLabel = new SpruceLabelOption(testimonialKey, Text.translatable(testimonialKey).styled(style -> style.withColor(0xDEDEDE)), false);

			this.list.addSingleOptionEntry(restrictionsSeparator);
			this.list.addSingleOptionEntry(testimonialLabel);
		}

		this.addDrawableChild(this.list);
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 154, this.height - 28), 150, 20, Text.translatable("platform_migration_warning.open_quilt_website"),
			btn -> Util.getOperatingSystem().open("https://quiltmc.org")).asVanilla());
		this.addDrawableChild(new SpruceButtonWidget(Position.of(this, this.width / 2 + 4, this.height - 28), 150, 20, SpruceTexts.GUI_DONE,
			btn -> this.client.setScreen(this.parent)).asVanilla());
	}

	@Override
	public void renderTitle(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

	@Override
	public void renderBackground(MatrixStack matrices, int vOffset) {
		normalBackground.render(matrices, this, vOffset);
	}

	@Override
	public void closeScreen() {
		this.client.setScreen(this.parent);
	}

	public record ModDeveloper(String author, List<Text> mods) {}
}

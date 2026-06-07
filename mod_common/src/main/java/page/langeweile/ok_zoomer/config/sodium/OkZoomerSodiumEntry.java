package page.langeweile.ok_zoomer.config.sodium;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import page.langeweile.ok_zoomer.config.screen.OkZoomerConfigScreen;
import page.langeweile.ok_zoomer.utils.ModUtils;

public class OkZoomerSodiumEntry implements ConfigEntryPoint {
	@Override
	public void registerConfigLate(ConfigBuilder builder) {
		builder.registerOwnModOptions()
			.setIcon(ModUtils.id("textures/sodium/icon.png"))
			.setColorTheme(builder.createColorTheme()
				.setBaseThemeRGB(0xffdc5d))
			.addPage(builder.createExternalPage()
				.setName(Component.translatable("config.ok_zoomer.sodium.page.zoom"))
				.setScreenConsumer(screen -> Minecraft.getInstance().gui.setScreen(new OkZoomerConfigScreen(screen))));
	}
}

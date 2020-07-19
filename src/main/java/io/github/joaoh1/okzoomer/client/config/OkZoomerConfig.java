package io.github.joaoh1.okzoomer.client.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

public class OkZoomerConfig {
	public static boolean isConfigLoaded = false;
	public static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("okzoomer-next.json5");
	
	private static final AnnotatedSettings annotatedSettings = AnnotatedSettings.builder()
		.useNamingConvention(SettingNamingConvention.SNAKE_CASE)
		.build();
	private static final OkZoomerConfigPojo pojo = new OkZoomerConfigPojo();
	public static final ConfigTree tree = ConfigTree.builder()
		.applyFromPojo(pojo, annotatedSettings)
		.build();
	
	private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

	public static void loadModConfig() {
		if (Files.exists(configPath)) {
			try {
				annotatedSettings.applyToNode(tree, pojo);
				FiberSerialization.deserialize(tree, Files.newInputStream(configPath), serializer);
				isConfigLoaded = true;
			} catch (IOException | FiberException e) {
				e.printStackTrace();
			}
		} else {
			saveModConfig();
			isConfigLoaded = true;
		}
	}

	public static void saveModConfig() {
		try {
			annotatedSettings.applyToNode(tree, pojo);
			FiberSerialization.serialize(tree, Files.newOutputStream(configPath), serializer);
		} catch (IOException | FiberException e) {
			e.printStackTrace();
		}
	}
}
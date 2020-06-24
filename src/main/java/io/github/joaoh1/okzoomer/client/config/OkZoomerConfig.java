package io.github.joaoh1.okzoomer.client.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.minecraft.util.Identifier;

public class OkZoomerConfig {
	public static final Path configPath = Paths.get("./config/ok-zoomer-next.json5");
	
	private static final OkZoomerConfigPojo pojo = new OkZoomerConfigPojo();
	public static final ConfigTree tree = ConfigTree.builder()
		.applyFromPojo(pojo,
			AnnotatedSettings.builder()
				.useNamingConvention(SettingNamingConvention.SNAKE_CASE)
				.build())
		.build();
	
	private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

	public static void loadModConfig() {
		if (Files.exists(configPath)) {
			try {
				FiberSerialization.deserialize(tree, Files.newInputStream(configPath), serializer);
			} catch (IOException | FiberException e) {
				e.printStackTrace();
			}
		} else {
			saveModConfig();
		}
	}

	public static void saveModConfig() {
		try {
			FiberSerialization.serialize(tree, Files.newOutputStream(configPath), serializer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
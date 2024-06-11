package io.github.ennuil.ok_zoomer.wrench_wrapper;

import io.github.ennuil.ok_zoomer.wrench_wrapper.fabric.FabricWrapper;
import io.github.ennuil.ok_zoomer.wrench_wrapper.quilt.QuiltWrapper;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.ReflectiveConfig;

public class WrenchWrapper {
	@NotNull
	public static <C extends ReflectiveConfig> C create(String family, String id, Class<C> configCreatorClass) {
		if (WrenchWrapper.classExists("org.quiltmc.loader.api.QuiltLoader")) {
			return QuiltWrapper.create(family, id, configCreatorClass);
		} else if (WrenchWrapper.classExists("net.fabricmc.loader.FabricLoader")) {
			return FabricWrapper.create(family, id, configCreatorClass);
		} else {
			throw new IllegalStateException("Neither Quilt nor Fabric detected, cannot create Config Instance for %s!".formatted(configCreatorClass.getName()));
		}
	}

	public static boolean classExists(String className) {
		try {
			var clazz = Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}

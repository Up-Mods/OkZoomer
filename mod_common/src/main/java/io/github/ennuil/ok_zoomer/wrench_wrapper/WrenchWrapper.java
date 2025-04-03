package io.github.ennuil.ok_zoomer.wrench_wrapper;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.implementor_api.ConfigEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class WrenchWrapper {
	public static <C extends ReflectiveConfig> C create(String family, String id, Class<C> configCreatorClass) {
		try {
			if (WrenchWrapper.getClass("org.quiltmc.loader.api.QuiltLoader") != null) {
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.quilt.QuiltWrapper"));
				return (C) clazz.getMethod("create", String.class, String.class, Class.class).invoke(null, family, id, configCreatorClass);
			} else if (WrenchWrapper.getClass("net.fabricmc.loader.FabricLoader") != null
				&& WrenchWrapper.getClass("net.minecraftforge.common.MinecraftForge") == null) {
				// The above check immunizes Wrench Wrapper's wrapper against Sinytra Connector
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.fabric.FabricWrapper"));
				return (C) clazz.getMethod("create", String.class, String.class, Class.class).invoke(null, family, id, configCreatorClass);
			} else if (WrenchWrapper.getClass("net.minecraftforge.common.MinecraftForge") != null) {
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.forge.ForgeWrapper"));
				return (C) clazz.getMethod("create", String.class, String.class, Class.class).invoke(null, family, id, configCreatorClass);
			} else {
				throw new IllegalStateException("Neither Quilt, Fabric nor Forge detected, cannot create Config Instance for %s!".formatted(configCreatorClass.getName()));
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalStateException("How did you do this!!!!! " + e);
		}
	}

	public static ConfigEnvironment getConfigEnvironment() {
		try {
			if (WrenchWrapper.getClass("org.quiltmc.loader.api.QuiltLoader") != null) {
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.quilt.QuiltWrapper"));
				return (ConfigEnvironment) clazz.getMethod("getConfigEnvironment").invoke(null);
			} else if (WrenchWrapper.getClass("net.fabricmc.loader.FabricLoader") != null
				&& WrenchWrapper.getClass("net.minecraftforge.common.MinecraftForge") == null) {
				// The above check immunizes Wrench Wrapper's wrapper against Sinytra Connector
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.fabric.FabricWrapper"));
				return (ConfigEnvironment) clazz.getMethod("getConfigEnvironment").invoke(null);
			} else if (WrenchWrapper.getClass("net.minecraftforge.common.MinecraftForge") != null) {
				var clazz = Objects.requireNonNull(WrenchWrapper.getClass("io.github.ennuil.ok_zoomer.wrench_wrapper.forge.ForgeWrapper"));
				return (ConfigEnvironment) clazz.getMethod("getConfigEnvironment").invoke(null);
			} else {
				throw new IllegalStateException("Neither Quilt, Fabric nor NeoForge detected, cannot get the Config Environment!");
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("How did you do this!!!!! " + e);
		}
	}

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}

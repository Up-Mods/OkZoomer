package io.github.ennuil.ok_zoomer.utils;

import net.minecraft.resources.ResourceLocation;

public class ModUtils {
	public static final String MOD_NAMESPACE = "ok_zoomer";

	private static final ResourceLocation MOD_NAMESPACE_ID = ResourceLocation.fromNamespaceAndPath(MOD_NAMESPACE, "");

	public static ResourceLocation id(String path) {
		return MOD_NAMESPACE_ID.withPath(path);
	}
}

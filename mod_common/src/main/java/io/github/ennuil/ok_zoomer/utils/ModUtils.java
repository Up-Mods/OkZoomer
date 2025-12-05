package io.github.ennuil.ok_zoomer.utils;

import net.minecraft.resources.Identifier;

public class ModUtils {
	public static final String MOD_NAMESPACE = "ok_zoomer";

	private static final Identifier MOD_NAMESPACE_ID = Identifier.fromNamespaceAndPath(MOD_NAMESPACE, "");

	public static Identifier id(String path) {
		return MOD_NAMESPACE_ID.withPath(path);
	}
}

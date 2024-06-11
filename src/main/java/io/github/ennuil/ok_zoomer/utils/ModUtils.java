package io.github.ennuil.ok_zoomer.utils;

import net.minecraft.resources.ResourceLocation;

public class ModUtils {
	public static final String MOD_NAMESPACE = "ok_zoomer";

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_NAMESPACE, ResourceLocation.assertValidPath(MOD_NAMESPACE, path));
	}
}

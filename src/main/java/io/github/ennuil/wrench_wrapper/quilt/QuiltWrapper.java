package io.github.ennuil.wrench_wrapper.quilt;

import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.loader.api.config.v2.QuiltConfig;

public class QuiltWrapper {
	public static <C extends ReflectiveConfig> C create(String family, String id, Class<C> configCreatorClass) {
		return QuiltConfig.create(family, id, configCreatorClass);
	}
}

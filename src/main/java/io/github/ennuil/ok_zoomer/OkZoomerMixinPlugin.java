package io.github.ennuil.ok_zoomer;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

// TODO - Demolish this mixin plugin by doing a real port!
public class OkZoomerMixinPlugin implements IMixinConfigPlugin {
	private static final Logger LOGGER = LoggerFactory.getLogger("Ok Zoomer Mixins");
	private static String mixinPackage;

	@Override
	public void onLoad(String mixinPackage) {
		OkZoomerMixinPlugin.mixinPackage = mixinPackage;
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith(mixinPackage)) {
			var id = mixinClassName.substring(mixinPackage.length());
			id = id.substring(1, id.indexOf('.', 1));

			boolean connectorLoaded = FabricLoader.getInstance().isModLoaded("connector");
			if (id.equals("sintyra") && connectorLoaded) {
				LOGGER.warn("Loaded Sintyra-specific mixins!");
				return true;
			} else if (id.equals("fabric")) {
				return !connectorLoaded;
			}

			return id.equals("common");
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}

package io.github.ennuil.ok_zoomer.utils;

import net.neoforged.fml.ModList;

public class NorgeZoomUtils {
	// TODO - Bad! We need client tags for this!
	//public static final Predicate<ItemStack> IS_VALID_SPYGLASS = stack -> stack.is(ZoomUtils.ZOOM_DEPENDENCIES_TAG);

	public static void addInitialPredicates() {
		ZoomUtils.addSpyglassProvider(player -> player.getInventory().contains(ZoomUtils.ZOOM_DEPENDENCIES_TAG));
	}

	public static void defineSafeSmartOcclusion() {
		// If Sodium is enabled, then we have all the pillars required to make Smart Occlusion work smoothly
		if (ModList.get().isLoaded("sodium")) {
			ZoomUtils.enableSafeSmartOcclusion();
		}
	}
}

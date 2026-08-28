package page.langeweile.ok_zoomer.norge.utils;

import net.neoforged.fml.ModList;
import page.langeweile.ok_zoomer.utils.ZoomUtils;

public class NorgeZoomUtils {
	// TODO - Bad! We need client tags for this!
	//public static final Predicate<ItemStack> IS_VALID_SPYGLASS = stack -> stack.is(ZoomUtils.ZOOM_DEPENDENCIES_TAG);

	public static void addInitialPredicates() {
		ZoomUtils.addSpyglassProvider(player -> player.getInventory().contains(ZoomUtils.ZOOM_DEPENDENCIES_TAG));
	}

	public static void defineSafeSmartOcclusion() {
		// If Sodium is enabled, then we have all the pillars required to make Smart Occlusion work smoothly
		if (ModList.get().isLoaded("sodium")) {
			// Very Many Players causes https://github.com/Up-Mods/OkZoomer/issues/192, and we can't disable the bad optimization,
			// so don't do this.
			if (!ModList.get().isLoaded("vmp")) {
				ZoomUtils.enableSafeDistantEntities();
			} else {
				ZoomUtils.LOGGER.warn("Very Many Players has been detected. Safe Distant Entities will be disabled due to a VMP optimization that interacts badly with it.");
			}

			ZoomUtils.enableSafeSmartOcclusion();
		}
	}
}

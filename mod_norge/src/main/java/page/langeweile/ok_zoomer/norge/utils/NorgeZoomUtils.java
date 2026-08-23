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
			// Very Many Players causes https://github.com/Up-Mods/OkZoomer/issues/192, can't have that
			if (!ModList.get().isLoaded("vmp")) {
				ZoomUtils.enableSafeDistantEntities();
			}

			ZoomUtils.enableSafeSmartOcclusion();
		}
	}
}

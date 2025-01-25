package io.github.ennuil.ok_zoomer.compat;

import io.github.ennuil.ok_zoomer.utils.ForgeZoomUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.wispforest.accessories.api.AccessoriesCapability;

public class AccessoriesCompat {
	public static void init() {
		ZoomUtils.addSpyglassProvider(player -> AccessoriesCapability.get(player).isEquipped(ForgeZoomUtils.IS_VALID_SPYGLASS));
	}
}

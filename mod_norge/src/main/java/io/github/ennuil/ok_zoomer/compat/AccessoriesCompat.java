package io.github.ennuil.ok_zoomer.compat;

import io.github.ennuil.ok_zoomer.utils.NorgeZoomUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;
import io.wispforest.accessories.api.AccessoriesCapability;

public class AccessoriesCompat {
	public static void init() {
		ZoomUtils.addSpyglassProvider(player -> AccessoriesCapability.get(player).isEquipped(NorgeZoomUtils.IS_VALID_SPYGLASS));
	}
}

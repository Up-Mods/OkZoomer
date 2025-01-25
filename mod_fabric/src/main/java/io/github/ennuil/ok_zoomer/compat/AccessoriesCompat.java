package io.github.ennuil.ok_zoomer.compat;

import io.github.ennuil.ok_zoomer.utils.FabricZoomUtils;
import io.github.ennuil.ok_zoomer.utils.ZoomUtils;

public class AccessoriesCompat {
	public static void init() {
		ZoomUtils.addSpyglassProvider(player -> player.accessoriesCapability().isEquipped(FabricZoomUtils.IS_VALID_SPYGLASS));
	}
}

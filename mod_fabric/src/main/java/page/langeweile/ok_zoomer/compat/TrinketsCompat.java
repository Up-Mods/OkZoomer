package page.langeweile.ok_zoomer.compat;

import dev.emi.trinkets.api.TrinketsApi;
import org.ladysnake.cca.api.v3.component.ComponentProvider;
import page.langeweile.ok_zoomer.utils.FabricZoomUtils;
import page.langeweile.ok_zoomer.utils.ZoomUtils;

public class TrinketsCompat {
	public static void init() {
		ZoomUtils.addSpyglassProvider(player -> {
			// Trinkets inventory is an AutoSyncedComponent and therefore safe to query on the client
			return ((ComponentProvider) player).getComponent(TrinketsApi.TRINKET_COMPONENT).isEquipped(FabricZoomUtils.IS_VALID_SPYGLASS);
		});
	}
}

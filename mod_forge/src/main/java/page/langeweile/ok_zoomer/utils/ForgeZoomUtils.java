package page.langeweile.ok_zoomer.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.util.function.Predicate;

public class ForgeZoomUtils {
	public static float translation = 0.0F;
	public static float scale = 0.0F;

	// TODO - Bad! We need client tags for this!
	public static final Predicate<ItemStack> IS_VALID_SPYGLASS = stack -> stack.is(ZoomUtils.ZOOM_DEPENDENCIES_TAG);

	public static void addInitialPredicates() {
		ZoomUtils.addSpyglassProvider(player -> player.getInventory().hasAnyMatching(IS_VALID_SPYGLASS));
	}

	public static void defineSafeSmartOcclusion() {
		// We implemented the original vanilla hack (which Sodium supplants by checking for all camera changes) into Embeddium
		// Embeddium will scream about it for reasonable reasons (imagine if the patch broke? that would be bad!)
		// Therefore, we will scream about it too! ((Neo)Forge does not let you add an issue tracker link)
		if (ModList.get().isLoaded("embeddium")) {
			ZoomUtils.LOGGER.info("Mixining into Embeddium in order to implement a missing change that new Sodium updates implements. If you find any zoom issues, report it to us on our issue tracker! It should be fine though!");
			ZoomUtils.enableSafeSmartOcclusion();
		}
	}
}

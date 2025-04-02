package io.github.ennuil.ok_zoomer.utils;

import net.minecraft.world.item.ItemStack;

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
		// Embeddium doesn't have the improvements that Sodium 0.5.13 have
		// It is not safe to check for Sodium here, we need to check for a better mod that does not exist
		// So! Do not do the check!
	}
}
